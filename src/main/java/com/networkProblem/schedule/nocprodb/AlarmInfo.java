package com.networkProblem.schedule.nocprodb;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Builder
@Data
public class AlarmInfo {
    private String owner;
    private String alarmId;
    private String siteCode;
    private LocalDateTime occurredTime;
    private LocalDateTime endTime;
    private String reason;
}
