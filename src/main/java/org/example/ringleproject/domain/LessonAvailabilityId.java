package org.example.ringleproject.domain;


import java.io.Serializable;

public class LessonAvailabilityId implements Serializable {
    private Long lessonId;
    private Long availabilityId;

    public LessonAvailabilityId(Long lesson, Long availability) {
        this.lessonId = lesson;
        this.availabilityId = availability;
    }
}
