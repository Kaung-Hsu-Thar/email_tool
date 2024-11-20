package com.networkProblem.schedule.nocprodb;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlarmService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public List<AlarmInfo> getSiteDown(LocalDateTime fromTime) {
        return new ArrayList<>() {{
            add(AlarmInfo.builder()
                    .alarmId("ALARM_DOWN_001")
                    .owner("ZTE 1")
                    .siteCode("YGN1234")
                    .occurredTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .build());
            add(AlarmInfo.builder()
                    .alarmId("ALARM_DOWN_002")
                    .owner("ZTE")
                    .siteCode("YGN1234")
                    .occurredTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .build());
            add(AlarmInfo.builder()
                    .alarmId("ALARM_DOWN_003")
                    .owner("ZTE 1")
                    .siteCode("YGN1234")
                    .occurredTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .build());

        }};
    }

    public List<AlarmInfo> getSiteUp(LocalDateTime fromTime) {
        return new ArrayList<>() {{
            add(AlarmInfo.builder()
                    .alarmId("ALARM_UP_001")
                    .owner("ZTE 1")
                    .siteCode("YGN1234")
                    .occurredTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .endTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .reason("Fake reason")
                    .build());
            add(AlarmInfo.builder()
                    .alarmId("ALARM_UP_002")
                    .owner("ZTE")
                    .siteCode("YGN1234")
                    .occurredTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .endTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .reason("Fake reason")
                    .build());
            add(AlarmInfo.builder()
                    .alarmId("ALARM_UP_003")
                    .owner("ZTE 1")
                    .siteCode("YGN1234")
                    .occurredTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .endTime(LocalDateTime.parse("01-11-2024 00:00:00", formatter))
                    .reason("Fake reason")
                    .build());
        }};
    }
}