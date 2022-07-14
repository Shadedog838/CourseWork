package com.estore.api.estoreapi.ordering;

import java.util.Comparator;

import com.estore.api.estoreapi.models.Course;

/**
 * Comparator class for sorting a list of courses
 * by decreasing popularity.
 */
public class OrderByPopularity implements Comparator<Course> {

    @Override
    public int compare(Course c1, Course c2) {
        return c2.getStudentsEnrolled() - c1.getStudentsEnrolled();
    }
}
