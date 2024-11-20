package com.networkProblem.schedule.repository;

import com.networkProblem.schedule.model.PartnerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerResponseRepository extends JpaRepository<PartnerResponse,Long> {
    List<PartnerResponse> findBySiteCode(String siteCode);
    boolean existsByAlarmId(String alarmId);
    Optional<PartnerResponse> findByAlarmId(String alarmId);
    Optional<PartnerResponse> findBySiteCodeAndAlarmIdAndIsSendEmail(String siteCode, String alarmId, boolean isSendEmail);
}
