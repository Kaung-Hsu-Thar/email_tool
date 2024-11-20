package com.networkProblem.schedule.model;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;


@Entity(name = "email_tool_partner_response")
@Data

public class PartnerResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String alarmId;
    private String siteCode;

    // Timing details
    private LocalDateTime timeDown;
    private LocalDateTime timeUp;

    // Status & response details
    private boolean isSendEmail;
    private boolean responseReceived;
    private String alarmType;

    // Partner information
    @Column(nullable = false)
    private Long partnerId;
    @Column(nullable = false)
    private String email;

    @Column(length = 1000)
    private String partnerReply;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PartnerResponse() {
    }

    public PartnerResponse(String alarmId, String siteCode, LocalDateTime timeDown, LocalDateTime timeUp, boolean isSendEmail, boolean responseReceived, String alarmType, Long partnerId, String email, String partnerReply, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.alarmId = alarmId;
        this.siteCode = siteCode;
        this.timeDown = timeDown;
        this.timeUp = timeUp;
        this.isSendEmail = isSendEmail;
        this.responseReceived = responseReceived;
        this.alarmType = alarmType;
        this.partnerId = partnerId;
        this.email = email;
        this.partnerReply = partnerReply;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
