package org.example.ringleproject.domain;

import lombok.Getter;

@Getter
public enum LessonDuration {
    MIN30(30), MIN60(60);

    private final int minutes;

    LessonDuration(int minutes) {
        this.minutes = minutes;
    }

    public static LessonDuration from(int minutes) {
        for (LessonDuration d : values()) {
            if (d.getMinutes() == minutes) return d;
        }
        throw new IllegalArgumentException("Unsupported duration: " + minutes);
    }

}
