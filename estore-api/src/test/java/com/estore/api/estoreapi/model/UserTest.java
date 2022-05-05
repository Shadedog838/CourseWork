package com.estore.api.estoreapi.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.estore.api.estoreapi.persistence.CourseDAO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Tag("Model-tier")
public class UserTest {
    @Test
    void testUser() throws IOException {
        Course course1 = new Course(0, "title", 5, "something");
        Course course2 = new Course(1, "title1", 7, "something else");

        CourseDAO mockCourseDAO = mock(CourseDAO.class);
        when(mockCourseDAO.getCourse(0)).thenReturn(course1);
        when(mockCourseDAO.getCourse(1)).thenReturn(course2);

        Set<Integer> courses = new HashSet<>();
        courses.add(2);
        courses.add(5);
        Set<Integer> shoppingCart = new HashSet<>();
        shoppingCart.add(0);
        User user = new User("Joe12", courses, shoppingCart, "Joe", "something@gmail.com", "I live here", false);
        assertEquals(courses, user.getCourses());

        assertFalse(user.isBanned());
        user.setBanned(true);
        assertTrue(user.isBanned());

        assertArrayEquals(new Course[] { course1 }, user.getShoppingCart(mockCourseDAO));
        user.addCourseToShoppingCartByID(1);
        assertArrayEquals(new Course[] { course1, course2 }, user.getShoppingCart(mockCourseDAO));

        assertEquals("Joe", user.getName());
        user.setUsersName("Joe Mamma");
        assertEquals("Joe Mamma", user.getName());

        assertEquals("Joe12", user.getUserName());

        assertEquals("something@gmail.com", user.getEmail());
        user.setUsersEmail("somethingelse@gmail.com");
        assertEquals("somethingelse@gmail.com", user.getEmail());

        assertEquals("I live here", user.getAddress());
        user.setUsersAddress("Now I live here");
        assertEquals("Now I live here", user.getAddress());
    }
}