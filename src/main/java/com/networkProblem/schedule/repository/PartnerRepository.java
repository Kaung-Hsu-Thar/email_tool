package com.networkProblem.schedule.repository;

import com.networkProblem.schedule.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner,Long> {
    Partner findByPartnerName(String owner);

    Partner findByPartnerEmail(String senderEmail);
}
