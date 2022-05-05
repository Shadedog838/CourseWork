package com.estore.api.estoreapi.ordering;

import java.util.Comparator;

import com.estore.api.estoreapi.model.Course;

public class OrderByPrice implements Comparator<Course> {

    @Override
    public int compare(Course c1, Course c2) {
        return Double.compare(c2.getPrice(), c1.getPrice());
    }
}
