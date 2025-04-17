package org.example.ringleproject.domain;

import jakarta.persistence.JoinColumn;

import java.io.Serializable;
import java.util.Objects;

public class LessonAvailabilityId implements Serializable {

    @JoinColumn(name = "lesson")
    private Long lesson;

    @JoinColumn(name = "availability")
    private Long availability;

    public LessonAvailabilityId() {}

    public LessonAvailabilityId(Long lesson, Long availability) {
        this.lesson = lesson;
        this.availability = availability;
    }
}
