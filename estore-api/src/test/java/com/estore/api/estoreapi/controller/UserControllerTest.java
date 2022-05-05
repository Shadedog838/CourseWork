package com.estore.api.estoreapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.estore.api.estoreapi.controller.requests.AuthenticatedRequest;
import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;
import com.estore.api.estoreapi.persistence.CourseDAO;
import com.estore.api.estoreapi.persistence.UserDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Tag("Controller-tier")

public class UserControllerTest {

    private UserController userController;
    private UserDAO mockUserDAO;
    private CourseDAO mockCourseDAO;
    private User user;
    private User bannedUser;
    private Course course;

    private Course course2;
    private Course course3;

    @BeforeEach
    void setupUserController() {
        mockUserDAO = mock(UserDAO.class);
        mockCourseDAO = mock(CourseDAO.class);
        userController = new UserController(mockUserDAO, mockCourseDAO);
        Set<Integer> testSet1 = new HashSet<>();
        testSet1.add(3);
        Set<Integer> testSet2 = new HashSet<>();
        testSet2.add(4);
        Set<String> testTags = new HashSet<>();
        testTags.add("testTag");
        user = new User("Bob", testSet2, testSet1, null, null, null, false);
        bannedUser = new User("I'mBanned", testSet2, testSet1, null, null, null, true);
        course = new Course(99, null, "Test Title", 10, "Test Description", 0, testTags, new ArrayList<>());

        testTags = new HashSet<>();
        testTags.add("testTag2");
        course2 = new Course(5, null, "Course2", 11, "Test Description1", 0, testTags, new ArrayList<>());
        testTags = new HashSet<>();
        testTags.add("testTag");
        course3 = new Course(6, null, "Course3", 12, "Test Description2", 0, testTags, new ArrayList<>());
    }

    @Test
    void testLoginUser() {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockUserDAO.getUser(bannedUser.getUserName())).thenReturn(bannedUser);

        ResponseEntity<User> response = userController.getUserAccount(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());

        response = userController.getUserAccount(bannedUser);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateUserFailed() throws IOException {
        when(mockUserDAO.updateUser(user)).thenReturn(null);
        ResponseEntity<User> response = userController.getUserAccount(user);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testGetUserCourses() throws IOException {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockUserDAO.getUser(bannedUser.getUserName())).thenReturn(bannedUser);
        when(mockCourseDAO.getCourse(3)).thenReturn(course);

        ResponseEntity<Course[]> response = userController.getUserCourses(user.getUserName());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(user.getCourses().stream().map(i -> mockCourseDAO.getCourse(i)).toArray(Course[]::new),
                response.getBody());

        response = userController.getUserCourses("Alice");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = userController.getUserCourses(bannedUser.getUserName());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetRecommendedCourses() {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockUserDAO.getUser(bannedUser.getUserName())).thenReturn(bannedUser);
        when(mockCourseDAO.getCourse(4)).thenReturn(course);
        when(mockCourseDAO.getCourse(5)).thenReturn(course2);
        when(mockCourseDAO.getCourse(6)).thenReturn(course3);
        when(mockCourseDAO.getRecommendedCoursesForUser(user)).thenReturn(new Course[] { course3 });

        ResponseEntity<Course[]> response = userController.getRecommendedCourses(user.getUserName(), 2);

        Course[] expected = new Course[1];
        expected[0] = course3;

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(expected, response.getBody());

        response = userController.getRecommendedCourses("Alice", 2);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = userController.getRecommendedCourses(bannedUser.getUserName(), 2);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    }

    @Test
    void testGetUserShoppingCart() throws IOException {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockCourseDAO.getCourse(4)).thenReturn(course);

        ResponseEntity<Course[]> response = userController.getUserShoppingCart(user.getUserName());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(user.getShoppingCart(mockCourseDAO), response.getBody());

        response = userController.getUserShoppingCart("Alice");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testRegisterUser() throws IOException {
        when(mockUserDAO.createUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testFailedRegister() throws IOException {
        when(mockUserDAO.createUser(user)).thenReturn(null);
        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        when(mockUserDAO.createUser(user)).thenThrow(new IOException());
        response = userController.createUser(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testUpdateUser() throws IOException {
        when(mockUserDAO.updateUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser(new AuthenticatedRequest<>(user, user.getUserName()));
        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    void testFailedUpdate() throws IOException {
        when(mockUserDAO.updateUser(user)).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(new AuthenticatedRequest<>(user, user.getUserName()));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        when(mockUserDAO.updateUser(user)).thenThrow(new IOException());
        response = userController.updateUser(new AuthenticatedRequest<>(user, user.getUserName()));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    void testUpdateUserBanned() throws IOException {
        when(mockUserDAO.updateUser(user)).thenReturn(null);
        user.setBanned(true);

        ResponseEntity<User> response = userController.updateUser(new AuthenticatedRequest<>(user, user.getUserName()));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateUserForbidden() throws IOException {
        when(mockUserDAO.updateUser(user)).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(new AuthenticatedRequest<>(user, "Alice"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetUser() throws IOException {
        when(mockUserDAO.getUser(bannedUser.getUserName())).thenReturn(bannedUser);
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);

        ResponseEntity<User> response = userController.getUser(user.getUserName());
        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = userController.getUser(bannedUser.getUserName());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testFailedGetUser() throws IOException {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(null);

        ResponseEntity<User> response = userController.getUser(user.getUserName());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testBanUser() throws IOException {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockUserDAO.updateUser(user)).thenReturn(user);
        ResponseEntity<User> updatedUser = userController.banUser(user.getUserName(), "Admin");
        assertEquals(HttpStatus.CREATED, updatedUser.getStatusCode());
        // because User is a reference type, both the returned user and the test user
        // should be the same object
        // since the test user is returned by the mock object
        assertEquals(user, updatedUser.getBody());
    }

    @Test
    void testBanUserForbidden() throws IOException {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockUserDAO.updateUser(user)).thenReturn(user);
        ResponseEntity<User> updatedUser = userController.banUser(user.getUserName(), "User");
        assertEquals(HttpStatus.FORBIDDEN, updatedUser.getStatusCode());
    }

    @Test
    void testBanUserNotFound() throws IOException {
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(null);
        ResponseEntity<User> updatedUser = userController.banUser(user.getUserName(), "Admin");
        assertEquals(HttpStatus.NOT_FOUND, updatedUser.getStatusCode());
    }

    @Test
    void testUnbanUser() throws IOException {
        user.setBanned(true);
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockUserDAO.updateUser(user)).thenReturn(user);
        ResponseEntity<User> updatedUser = userController.unbanUser(user.getUserName(), "Admin");
        assertEquals(HttpStatus.CREATED, updatedUser.getStatusCode());
        // because User is a reference type, both the returned user and the test user
        // should be the same object
        // since the test user is returned by the mock object
        assertEquals(user, updatedUser.getBody());
    }

    @Test
    void testUnbanUserForbidden() throws IOException {
        user.setBanned(true);
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(user);
        when(mockUserDAO.updateUser(user)).thenReturn(user);
        ResponseEntity<User> updatedUser = userController.unbanUser(user.getUserName(), "User");
        assertEquals(HttpStatus.FORBIDDEN, updatedUser.getStatusCode());
    }

    @Test
    void testUnbanUserNotFound() throws IOException {
        user.setBanned(true);
        when(mockUserDAO.getUser(user.getUserName())).thenReturn(null);
        ResponseEntity<User> updatedUser = userController.unbanUser(user.getUserName(), "Admin");
        assertEquals(HttpStatus.NOT_FOUND, updatedUser.getStatusCode());
    }

    @Test
    void testGetUsers() {
        User[] users = new User[] { user };
        when(mockUserDAO.getAllUsers()).thenReturn(users);
        ResponseEntity<User[]> response = userController.getAllUsers("Admin");
        assertArrayEquals(users, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUsersForbidden() {
        User[] users = new User[] { user };
        when(mockUserDAO.getAllUsers()).thenReturn(users);
        ResponseEntity<User[]> response = userController.getAllUsers("User");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateUserCourses() throws IOException {
        when(mockUserDAO.updateUserCourses(user.getUserName(), user.getCourses())).thenReturn(user);

        ResponseEntity<User> response = userController.updateUserCourses(user);
        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFailedUpdateUserCourses() throws IOException {
        when(mockUserDAO.updateUserCourses(user.getUserName(), user.getCourses())).thenReturn(null);
        ResponseEntity<User> response = userController.updateUserCourses(user);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        when(mockUserDAO.updateUserCourses(user.getUserName(), user.getCourses())).thenThrow(new IOException());
        response = userController.updateUserCourses(user);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}