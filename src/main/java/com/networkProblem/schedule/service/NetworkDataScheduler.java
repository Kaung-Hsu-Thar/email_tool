package com.networkProblem.schedule.service;

import com.networkProblem.schedule.model.Partner;
import com.networkProblem.schedule.model.PartnerResponse;
import com.networkProblem.schedule.nocprodb.AlarmInfo;
import com.networkProblem.schedule.nocprodb.AlarmService;
import com.networkProblem.schedule.repository.PartnerRepository;
import com.networkProblem.schedule.repository.PartnerResponseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@Transactional
public class NetworkDataScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailListenerService emailListenerService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerResponseRepository partnerResponseRepository;

    private static final int MAX_RETRIES = 3;

    @Scheduled(cron = "0 * * ? * *")
    public void checkNetworkDataConnection() {
        log.info("Scheduled task triggered at: {}", LocalDateTime.now());
        processAlarms(alarmService.getSiteDown(LocalDateTime.now().minusHours(1)), "DOWN", "site_down");
        processAlarms(alarmService.getSiteUp(LocalDateTime.now().minusHours(1)), "UP", "site_up");

        emailListenerService.listenForReplies();
    }

    private void processAlarms(List<AlarmInfo> alarms, String alarmType, String templateName) {
        for (AlarmInfo alarm : alarms) {
            String alarmPrefix = alarmType.equals("DOWN") ? "ALARM_DOWN_" : "ALARM_UP_";
            if (alarm.getAlarmId().startsWith(alarmPrefix)) {
                Partner partner = findPartnerByOwner(alarm.getOwner());

                if (partner != null && !hasSiteBeenProcessed(alarm.getAlarmId())) {

                    // Check if email was already sent for the current site and alarm type
                    boolean emailAlreadySent = isEmailAlreadySent(alarm.getSiteCode(), alarm.getAlarmId(), alarmType);
                    if (emailAlreadySent) {
                        log.info("Email already sent for site: {} with alarmId: {}", alarm.getSiteCode(), alarm.getAlarmId());
                        return;
                    }

                    boolean emailSent = sendEmailWithRetry(() ->
                            emailService.sendTemplateNotification(partner, alarm, templateName)
                    );

                    if (emailSent) {
                        PartnerResponse partnerResponse = new PartnerResponse();
                        partnerResponse.setAlarmId(alarm.getAlarmId());
                        partnerResponse.setSiteCode(alarm.getSiteCode());
                        partnerResponse.setTimeDown(alarm.getOccurredTime());
                        if ("UP".equals(alarmType)) {
                            partnerResponse.setTimeUp(alarm.getEndTime());
                        }
                        partnerResponse.setReason(alarm.getReason());
                        partnerResponse.setSendEmail(true);
                        partnerResponse.setAlarmType(alarmType);
                        partnerResponse.setPartnerId(partner.getPartnerId());
                        partnerResponse.setEmail(partner.getPartnerEmail());
                        partnerResponse.setCreatedAt(LocalDateTime.now());
                        partnerResponseRepository.save(partnerResponse);
                        log.info("Email sent successfully to {} with alarmId: {}" , alarm.getOwner(), alarm.getAlarmId());
                    } else {
                       // log.error("Email failed after {} retries for alarmId: {}", MAX_RETRIES, alarm.getAlarmId());
                        //TODO warning to developer check why email send fail 3 times
                    }
                }
            }
        }
    }

    private boolean isEmailAlreadySent(String siteCode, String alarmId, String alarmType) {
        // Check the email status for the current site and alarmId
        Optional<PartnerResponse> response = partnerResponseRepository
                .findBySiteCodeAndAlarmIdAndIsSendEmail(siteCode, alarmId, true);

        if (response.isPresent()) {
            log.info("Email already sent for siteCode: {} with alarmId: {}", siteCode, alarmId);
            return true;
        }
        return false;
    }

    public boolean hasSiteBeenProcessed(String alarmId) {
        boolean exists = partnerResponseRepository.existsByAlarmId(alarmId);
        log.info("Checking if site with alarmId: {} has been processed: {}", alarmId, exists);
        return exists;
    }

    private Partner findPartnerByOwner(String owner) {
        return partnerRepository.findByPartnerName(owner);
    }

    private boolean sendEmailWithRetry(SendEmailOperation operation) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            attempt++;
            String result = operation.send();
            if ("SUCCESS".equals(result)) {
                return true;
            }
            log.info("Email send failed, retry attempt {}", attempt);
        }
        return false;
    }

    @FunctionalInterface
    private interface SendEmailOperation {
        String send();
    }
}
