package com.networkProblem.schedule.service;

import com.networkProblem.schedule.model.EmailTemplate;
import com.networkProblem.schedule.model.Partner;
import com.networkProblem.schedule.nocprodb.AlarmInfo;
import com.networkProblem.schedule.repository.EmailTemplateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class EmailService {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private JavaMailSender javamailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Format LocalDateTime to String
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    // Utility to populate placeholders in templates
    private String populateTemplate(String template, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    public String sendTemplateNotification(Partner partner, AlarmInfo alarmInfo, String templateName) {
        EmailTemplate emailTemplate = emailTemplateRepository.findByTemplateName(templateName);
        if (emailTemplate == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }

        // Create a map of placeholders and their corresponding values
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("TCO_name", partner.getPartnerName());
        placeholders.put("Site_code", alarmInfo.getSiteCode());
        placeholders.put("Time_down", formatDateTime(alarmInfo.getOccurredTime()));

        if ("site_up".equals(templateName)) {
            placeholders.put("Time_up", formatDateTime(alarmInfo.getEndTime()));
            placeholders.put("Alarm_id", alarmInfo.getAlarmId());
        }

        // Populate the subject and body with placeholders
        String subject = populateTemplate(emailTemplate.getSubject(), placeholders);
        String body = populateTemplate(emailTemplate.getBody(), placeholders);

        // Send the email
        return sendHtmlEmail(partner.getPartnerEmail(), subject, body);
    }

    private String sendHtmlEmail(String to, String subject, String body) {
        try {
            // Create a MimeMessage
            MimeMessage message = javamailSender.createMimeMessage();

            // Create a MimeMessageHelper to set the message's parameters
            MimeMessageHelper helper = new MimeMessageHelper(message, true);


            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            // Set the email content type as HTML
            helper.setText(body, true);

            // Send the email
            javamailSender.send(message);
            return "SUCCESS";
        } catch (MessagingException | MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return "FAIL";
        }
    }
}
