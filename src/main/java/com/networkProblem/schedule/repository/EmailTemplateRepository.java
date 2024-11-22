package com.networkProblem.schedule.repository;

import com.networkProblem.schedule.model.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    EmailTemplate findByTemplateName(String siteDown);
}
