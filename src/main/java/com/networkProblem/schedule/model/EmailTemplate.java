package com.networkProblem.schedule.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "email_template")
@Data
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String templateName;

    private String subject;

    @Column(nullable = false, length = 1000)
    private String body;

    private String placeholder;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
