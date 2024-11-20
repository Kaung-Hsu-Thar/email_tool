package com.networkProblem.schedule.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "email_tool_partner")
@Data

public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    @Column(nullable = false)
    private String partnerName;

    @Column(nullable = false, unique = true)
    private String partnerEmail;

    public Partner(){

    }

    public Partner(Long partnerId, String partnerName, String partnerEmail) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerEmail = partnerEmail;
    }


}