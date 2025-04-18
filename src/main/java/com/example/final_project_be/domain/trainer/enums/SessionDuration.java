package com.example.final_project_be.domain.trainer.enums;

import lombok.Getter;

@Getter
public enum SessionDuration {
    THIRTY_MINUTES(30),
    SIXTY_MINUTES(60),
    NINETY_MINUTES(90);

    private final int minutes;

    SessionDuration(int minutes) {
        this.minutes = minutes;
    }
} 