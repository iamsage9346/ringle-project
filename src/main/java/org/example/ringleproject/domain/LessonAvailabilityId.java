package org.example.ringleproject.domain;

import java.io.Serializable;
import java.util.Objects;

public class LessonAvailabilityId implements Serializable {

    private Long lesson;
    private Long availability;

    public LessonAvailabilityId() {}

    public LessonAvailabilityId(Long lesson, Long availability) {
        this.lesson = lesson;
        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonAvailabilityId)) return false;
        LessonAvailabilityId that = (LessonAvailabilityId) o;
        return Objects.equals(lesson, that.lesson) &&
                Objects.equals(availability, that.availability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lesson, availability);
    }
}
