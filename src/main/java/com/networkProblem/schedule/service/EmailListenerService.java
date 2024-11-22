package com.networkProblem.schedule.service;

import com.networkProblem.schedule.model.Partner;
import com.networkProblem.schedule.model.PartnerResponse;
import com.networkProblem.schedule.repository.EmailTemplateRepository;
import com.networkProblem.schedule.repository.PartnerResponseRepository;
import com.networkProblem.schedule.repository.PartnerRepository;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.search.ReceivedDateTerm;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

@Service
@Transactional
@Slf4j
public class EmailListenerService {
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private PartnerResponseRepository partnerResponseRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Value("${email.imap.host}")
    private String imapHost;

    @Value("${email.imap.username}")
    private String emailUsername;

    @Value("${email.imap.password}")
    private String emailPassword;

    public void listenForReplies() {
        log.info("Listening for replies...");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        Session emailSession = Session.getDefaultInstance(properties);
        try {
            Store store = emailSession.getStore("imap");
            store.connect(imapHost, emailUsername, emailPassword);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Filter emails from the last 24 hours
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date oneDayAgo = calendar.getTime();

            ReceivedDateTerm recentEmails = new ReceivedDateTerm(ReceivedDateTerm.GT, oneDayAgo);
            Message[] messages = inbox.search(recentEmails);

            log.info("Retrieved " + messages.length + " messages from the last 24 hours.");

            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String content = getEmailContent(mimeMessage);

                    log.info("Email content: " + content);

                    // Extract sender email
                    String senderEmail = mimeMessage.getFrom()[0].toString();
                    try {
                        senderEmail = new InternetAddress(senderEmail).getAddress();
                    } catch (Exception e) {
                        log.warn("Failed to parse sender email address: {}", senderEmail, e);
                        continue;
                    }
                    Partner partner = partnerRepository.findByPartnerEmail(senderEmail);

                    if (partner != null) {
                        String alarmId = extractAlarmId(content);
                        if (alarmId != null) {
                            // Check if the response for this alarmId exists in the database
                            Optional<PartnerResponse> existingResponse = partnerResponseRepository.findByAlarmId(alarmId);

                            if (existingResponse.isPresent() && !existingResponse.get().isResponseReceived()) {
                                String siteCode = extractField(content, "Site code:");
                                String timeDown = extractField(content, "Time down:");
                                String timeUp = extractField(content, "Time up:");
                                String rootCause = extractField(content, "Root cause:");

                                log.info("Extracted details - AlarmId: {}, SiteCode: {}, TimeDown: {}, TimeUp: {}, RootCause: {}",
                                        alarmId, siteCode, timeDown, timeUp, rootCause);

                                // Adding DateTime parsing
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                                LocalDateTime parsedTimeDown = LocalDateTime.parse(timeDown, formatter);
                                LocalDateTime parsedTimeUp = LocalDateTime.parse(timeUp, formatter);


                                if (siteCode != null && timeDown != null && timeUp != null && rootCause != null) {

                                    PartnerResponse partnerResponse = existingResponse.get();
                                    partnerResponse.setResponseReceived(true);
                                    partnerResponse.setUpdatedAt(LocalDateTime.now());
                                    partnerResponse.setPartnerReply(
                                            "Site code: " + siteCode + "\n Time down: " +
                                                    LocalDateTime.parse(timeDown, formatter) + "\n Time up: " +
                                                    LocalDateTime.parse(timeUp, formatter) + "\n Root cause: " + rootCause
                                    );


                                    partnerResponseRepository.save(partnerResponse);
                                    log.info("Updated partner response for AlarmId: {}", alarmId);
                                } else {
                                    log.warn("Incomplete details in reply email for AlarmId: {}", alarmId);
                                }
                            } else {
                                log.warn("No alarmId found or response already received for AlarmId: {}", alarmId);
                            }
                        } else {
                            log.warn("No alarmId found in email from: {}", senderEmail);
                        }
                    } else {
                        log.warn("No partner found for email: {}", senderEmail);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while listening for replies: ", e);
        }
    }


    private String getEmailContent(MimeMessage mimeMessage) throws MessagingException, java.io.IOException {
        StringBuilder content = new StringBuilder();
        Object messageContent = mimeMessage.getContent();
        if (messageContent instanceof String) {
            content.append((String) messageContent);
        } else if (messageContent instanceof Multipart) {
            Multipart multipart = (Multipart) messageContent;
            for (int i = 0; i < multipart.getCount(); i++) {
                Part part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    content.append(part.getContent());
                }

            }
        }
        return content.toString();
    }

    private String extractAlarmId(String content) {
        String regex = "AlarmId:\\s*(\\S+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractField(String content, String fieldPrefix) {
        String regex = fieldPrefix + "\\s*([^\\n]*)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}