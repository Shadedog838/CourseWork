package com.estore.api.estoreapi.persistence;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Persistence-tier")
public class UserFileDAOTest {
    UserFileDAO userFileDAO;
    Course[] testCourses;
    User[] testUsers;
    ObjectMapper mockObjectMapper;
    CourseDAO mockCourseDAO;

    /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * 
     * @throws IOException
     */
    @BeforeEach
    public void setupCourseFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        mockCourseDAO = mock(CourseDAO.class);

        testCourses = new Course[3];
        testCourses[0] = new Course(99, "Discrete Math", 9.99, "This is a course about discrete math");
        testCourses[1] = new Course(100, "Linear Math", 8.99, "This is a course about linear math");
        testCourses[2] = new Course(101, "Stats Math", 10.99, "This is a course about stats math");

        Set<Integer> testSet1 = new HashSet<>();
        testSet1.add(99);
        Set<Integer> testSet2 = new HashSet<>();
        testSet2.add(100);
        testSet2.add(101);
        Set<Integer> testSet3 = new HashSet<>();
        testSet3.add(99);
        testSet3.add(100);
        testSet3.add(101);
        testUsers = new User[3];
        testUsers[0] = new User("Bob", testSet2, testSet1, null, null, null, false);
        testUsers[1] = new User("George", testSet3, testSet2, null, null, null, false);
        testUsers[2] = new User("Alice", testSet1, testSet3, null, null, null, false);

        when(mockCourseDAO.getCourse(99)).thenReturn(testCourses[0]);
        when(mockCourseDAO.getCourse(100)).thenReturn(testCourses[1]);
        when(mockCourseDAO.getCourse(101)).thenReturn(testCourses[2]);

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the course array above
        when(mockObjectMapper
                .readValue(new File("doesnt_matter.txt"), User[].class))
                .thenReturn(testUsers);
        userFileDAO = new UserFileDAO("doesnt_matter.txt", mockObjectMapper);
        userFileDAO.setCourseDAO(mockCourseDAO);
    }

    @Test
    public void testGetAllUsers() {
        User[] allUsers = userFileDAO.getAllUsers();
        assertEquals(testUsers.length, allUsers.length);
        Set<User> allUsersSet = Arrays.stream(allUsers).collect(Collectors.toSet());
        Set<User> testUsersSet = Arrays.stream(testUsers).collect(Collectors.toSet());
        assertEquals(testUsersSet, allUsersSet);
    }

    @Test
    public void testGetUser() {
        for (int i = 0; i < testUsers.length; i++) {
            User user = userFileDAO.getUser(testUsers[i].getUserName());
            assertEquals(user, testUsers[i]);
        }
    }

    @Test
    public void testGetUserCourse() throws IOException {
        for (int i = 0; i < testUsers.length; i++) {
            Set<Integer> courseIDs = userFileDAO.getUserCourse(testUsers[i].getUserName());
            Set<Integer> userCourseIDs = userFileDAO.getUserCourse(testUsers[i].getUserName());
            assertEquals(courseIDs.size(), userCourseIDs.size());

            assertEquals(courseIDs, userCourseIDs);
        }

        User dneUser = new User("Jeffery");
        assertNull(userFileDAO.getUserCourse(dneUser.getUserName()));
    }

    @Test
    public void testGetUserShoppingCart() throws IOException {
        for (int i = 0; i < testUsers.length; i++) {
            Course[] cart = userFileDAO.getUserShoppingCart(testUsers[i].getUserName());
            assertArrayEquals(cart, testUsers[i].getShoppingCart(mockCourseDAO));
        }

        User dneUser = new User("Jeffery");
        assertNull(userFileDAO.getUserShoppingCart(dneUser.getUserName()));
    }

    @Test
    public void testUpdateUserShoppingCart() throws IOException {
        User testUser = new User(testUsers[0].getUserName());
        Set<Integer> newCart = new HashSet<Integer>();
        newCart.add(100);
        testUser.updateShoppingCart(newCart);

        Course[] cartInitial = userFileDAO.getUserShoppingCart(testUsers[0].getUserName());
        assertArrayEquals(cartInitial, testUsers[0].getShoppingCart(mockCourseDAO));

        // perform the modification
        assertTrue(userFileDAO.updateUserShoppingCart(testUser.getUserName(), newCart));

        Course[] cartUpdated = userFileDAO.getUserShoppingCart(testUser.getUserName());
        assertArrayEquals(cartUpdated, testUser.getShoppingCart(mockCourseDAO));

        Set<Integer> oldCart = new HashSet<>();
        oldCart.add(99);

        // false condition
        assertFalse(userFileDAO.updateUserShoppingCart("Jeff", newCart));
    }

    @Test
    public void testCreateUser() throws IOException {
        User newUser = testUsers[0];
        assertNull(userFileDAO.createUser(newUser));

        newUser = new User("Jeffery");

        assertEquals(newUser, userFileDAO.createUser(newUser));
    }

    @Test
    public void testUpdateUser() throws IOException {
        User testUser = new User("Jeffery");

        assertNull(userFileDAO.updateUser(testUser));

        testUser = new User("George");

        // perform the modification
        assertEquals(testUser, (userFileDAO.updateUser(testUser)));
    }

    @Test
    public void testUpdateUserCourse() throws IOException {
        Set<Integer> newCourses = new HashSet<Integer>();
        newCourses.add(99);
        newCourses.add(101);
        assertEquals(null, userFileDAO.updateUserCourses("Jeffery", newCourses));

        // perform the modification
        assertEquals(testUsers[0], userFileDAO.updateUserCourses(testUsers[0].getUserName(), newCourses));
    }
}
