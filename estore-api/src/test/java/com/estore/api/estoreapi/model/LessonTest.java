package com.estore.api.estoreapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("model-tier")
public class LessonTest {
    @Test
    public void testLesson() {
        // Setup
        String expected_title = "JavaScript";
        String expected_video = "https://www.youtube.com/embed/Ho91a_GwYxs";

        // Invoke
        Lesson lesson = new Lesson(expected_title, expected_video);

        // Analyze
        assertEquals(expected_title, lesson.getTitle());
        assertEquals(expected_video, lesson.getVideo());
    }

    @Test
    public void testSetters() {
        // Setup
        Lesson lesson = new Lesson("CS", "youtube");
        lesson.setTitle("Biology");
        lesson.setVideo("youtube.com");

        // Analyze
        assertEquals(lesson.getTitle(), "Biology");
        assertEquals(lesson.getVideo(), "youtube.com");
    }

}
