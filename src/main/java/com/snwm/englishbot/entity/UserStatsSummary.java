package com.snwm.englishbot.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatsSummary {
    private int correctAttempts;
    private int incorrectAttempts;
    private double successRate;
    private LocalDateTime lastAttempt;
}
