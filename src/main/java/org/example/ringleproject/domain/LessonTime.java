package org.example.ringleproject.domain;

import lombok.Getter;

@Getter
public enum LessonTime {
    MIN30(30), MIN60(60);

    private final int minutes;

    LessonTime(int minutes) {
        this.minutes = minutes;
    }

}
