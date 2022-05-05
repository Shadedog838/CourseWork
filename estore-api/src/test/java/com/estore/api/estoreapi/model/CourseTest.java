package com.estore.api.estoreapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Tag("Model-tier")
public class CourseTest {
    @Test
    public void testCourse() {
        // Since this tests the constructor, all fields can be tested here.
        // In other words, there is no need for separate tests for each getter.
        // Setup
        int expected_id = 55;
        String expected_title = "Psychology";
        double expected_price = 9.99;
        String expected_description = "This is a course about psychology";

        // Invoke
        Course course = new Course(expected_id, expected_title, expected_price, expected_description);

        // Analyze
        assertEquals(expected_id, course.getId());
        assertEquals(expected_title, course.getTitle());
        assertEquals(expected_price, course.getPrice());
        assertEquals(expected_description, course.getDescription());
        assertEquals(new ArrayList<>(), course.getContent());
        assertEquals(new HashSet<>(), course.getTags());
        assertEquals(0, course.getStudentsEnrolled());
    }

    @Test
    public void testSetters() {
        // Setup
        Course course = new Course(2, "Linear Algebra", 10.99, "Course is about Linear Algebra");
        course.setPrice(15.99);
        course.setDescription("This is a Linear Algebra course");
        course.setStudentsEnrolled(55);

        Set<String> tags = new HashSet<>();
        tags.add("Electronics");
        course.setTags(tags);
        List<Lesson> content = new ArrayList<>();
        Lesson lesson = new Lesson("Math", "youtube");
        content.add(lesson);
        course.setContent(content);

        // Analyze
        assertTrue(course.getPrice() == 15.99);
        assertTrue(course.getDescription() == "This is a Linear Algebra course");
        assertTrue(course.getTags() == tags);
        assertTrue(course.getContent() == content);
    }
}