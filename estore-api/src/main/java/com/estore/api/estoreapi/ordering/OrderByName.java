package com.estore.api.estoreapi.ordering;

import java.util.Comparator;

import com.estore.api.estoreapi.model.Course;

/**
 * Comparator class for sorting a list of courses
 * alphabetically (a->z).
 */
public class OrderByName implements Comparator<Course> {

    @Override
    public int compare(Course c1, Course c2) {
        return c1.getTitle().compareTo(c2.getTitle());
    }
}
