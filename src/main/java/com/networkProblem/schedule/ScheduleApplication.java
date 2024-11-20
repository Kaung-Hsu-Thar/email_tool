package com.networkProblem.schedule;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class ScheduleApplication {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }

}
