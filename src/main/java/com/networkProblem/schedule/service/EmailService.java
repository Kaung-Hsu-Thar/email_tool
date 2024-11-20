package com.networkProblem.schedule.service;

import com.networkProblem.schedule.model.Partner;
import com.networkProblem.schedule.nocprodb.AlarmInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class EmailService {

    @Autowired
    private JavaMailSender javamailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Format LocalDateTime to String
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    // Send Site-Down Notification Email
    public String sendSiteDownNotification(Partner partner, AlarmInfo alarmInfo) {
        String partnerName = partner.getPartnerName();
        String siteCode = alarmInfo.getSiteCode();
        String occurredTime = formatDateTime(alarmInfo.getOccurredTime());

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "%s down at %s.\n" +
                        "Please check the site down reason and feedback to us.\n\n" +
                        "Thank you,\nMytel Team",
                partnerName, siteCode, occurredTime
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(partner.getPartnerEmail());
        message.setSubject(String.format("%s site down %s", partnerName, siteCode));
        message.setText(emailBody);

        try {
            javamailSender.send(message);
            return "SUCCESS";
        } catch (Exception e) {
            System.err.println("Failed to send site-down email: " + e.getMessage());
            return "FAIL";
        }
    }

    // Send Site-Up Notification Email
    public String sendSiteUpNotification(Partner partner, AlarmInfo alarmInfo) {
        String alarmId = alarmInfo.getAlarmId();
        String partnerName = partner.getPartnerName();
        String siteCode = alarmInfo.getSiteCode();
        String occurredTime = formatDateTime(alarmInfo.getOccurredTime());
        String endTime = formatDateTime(alarmInfo.getEndTime());

        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "%s up at %s.\n" +
                        "Period of time down is from: %s to %s.\n" +
                        "Please give feedback to us the root cause of this incident as template:\n" +
                        "- AlarmId:"+ alarmId +".\n"+
                        "- Site code: ……………….\n" +
                        "- Time down: …………..\n" +
                        "- Time up: ………………\n" +
                        "- Root cause: ……………….\n\n" +
                        "Thank you,\nMytel Team",
                partnerName, siteCode, endTime, occurredTime, endTime
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(partner.getPartnerEmail());
        message.setSubject(String.format("%s site up %s", partnerName, siteCode));
        message.setText(emailBody);

        try {
            javamailSender.send(message);
            return "SUCCESS";
        } catch (Exception e) {
            System.err.println("Failed to send site-up email: " + e.getMessage());
            return "FAIL";
        }
    }
}