package com.estore.api.estoreapi.controller;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("Controller-tier")
public class CourseControllerTest {
    private final User admin = new User("admin");
    private CourseController courseController;
    private CourseDAO mockCourseDAO;
    private Course course;
    private UserDAO mockUserDAO;

    @BeforeEach
    void setupCourseController() {
        mockCourseDAO = mock(CourseDAO.class);
        courseController = new CourseController(mockUserDAO, mockCourseDAO);
        course = new Course(99, null, "Test Title", 10, "Test Description", 0, new HashSet<>(), new ArrayList<>());
    }

    @Test
    void testCreateUser() throws IOException {
        // mock course to create
        // simulates successful course creation
        when(mockCourseDAO.createCourse(course)).thenReturn(course);

        ResponseEntity<Course> response = courseController
                .createCourse(new AuthenticatedRequest(course, admin.getUserName()));

        // the course creation should succeed, so the response should be created
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(course, response.getBody());
    }

    @Test
    void testCreateCourseFail() throws IOException {
        // mock course to create
        // simulates failure to create the course
        when(mockCourseDAO.createCourse(course)).thenReturn(null);

        ResponseEntity<Course> response = courseController
                .createCourse(new AuthenticatedRequest(course, admin.getUserName()));

        // the course creation should fail, so the response should be a conflict
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testCreateCourseInternalError() throws IOException {
        when(mockCourseDAO.createCourse(course)).thenThrow(new IOException());

        ResponseEntity<Course> response = courseController
                .createCourse(new AuthenticatedRequest(course, admin.getUserName()));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testCourseCreateForbidden() throws IOException {
        // mock course to create
        User user = new User("otherUser");
        // simulates successful course creation
        when(mockCourseDAO.createCourse(course)).thenReturn(null);

        ResponseEntity<Course> response = courseController
                .createCourse(new AuthenticatedRequest(course, user.getUserName()));

        // the course creation should succeed, so the response should be created
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetCourse() throws IOException {
        // Setup
        int courseId = 99;
        // When the same id is passed in, our mock Course DAO will return the Course
        // object
        when(mockCourseDAO.getCourse(courseId)).thenReturn(course);

        // Invoke
        ResponseEntity<Course> response = courseController.getCourse(course.getId());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course, response.getBody());
    }

    @Test
    public void testGetCourseNotFound() throws Exception {
        // Setup
        int courseId = 25;
        // When the same id is passed in, our mock Course DAO will return null,
        // simulating
        // no course found
        when(mockCourseDAO.getCourse(courseId)).thenReturn(null);

        // Invoke
        ResponseEntity<Course> response = courseController.getCourse(courseId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetCourses() throws IOException {
        // Setup
        Course[] courses = new Course[2];
        courses[0] = course;
        courses[1] = new Course(100, null, "Course 100", 12, "Course 100 Description", 0, new HashSet<>(),
                new ArrayList<>());

        // When getCourses is called return the courses created above
        when(mockCourseDAO.getCourses()).thenReturn(courses);

        // Invoke
        ResponseEntity<Course[]> response = courseController.getCourses();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courses, response.getBody());

    }

    @Test
    public void testUpdateCourse() throws IOException { // updateCourse may throw IOException
        // Setup
        // when updateCourse is called, return true simulating successful
        // update and save
        when(mockCourseDAO.updateCourse(course)).thenReturn(course);
        course.setTitle("course2");

        // Invoke
        ResponseEntity<Course> response = courseController
                .updateCourse(new AuthenticatedRequest(course, admin.getUserName()));

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course, response.getBody());
    }

    @Test
    public void testUpdateCourseFailed() throws IOException { // updateCourse may throw IOException
        // Setup
        // when updateCourse is called, return true simulating successful
        // update and save
        when(mockCourseDAO.updateCourse(course)).thenReturn(null);

        // Invoke
        ResponseEntity<Course> response = courseController
                .updateCourse(new AuthenticatedRequest(course, admin.getUserName()));

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateCourseHandleException() throws IOException { // updateCourse may throw IOException
        // Setup
        // When updateCourse is called on the Mock Course DAO, throw an IOException
        doThrow(new IOException()).when(mockCourseDAO).updateCourse(course);

        // Invoke
        ResponseEntity<Course> response = courseController
                .updateCourse(new AuthenticatedRequest(course, admin.getUserName()));

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUpdateCourseForbidden() throws IOException {
        // mock course to create
        User user = new User("otherUser");
        // simulates successful course creation
        when(mockCourseDAO.createCourse(course)).thenReturn(null);

        ResponseEntity<Course> response = courseController
                .updateCourse(new AuthenticatedRequest(course, user.getUserName()));

        // the course creation should succeed, so the response should be created
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    }

    @Test
    public void testDeleteCourse() throws IOException { // deleteCourse may throw IOException
        // Setup
        int CourseId = 99;
        // when deleteCourse is called return true, simulating successful deletion
        when(mockCourseDAO.deleteCourse(CourseId)).thenReturn(true);

        // Invoke
        ResponseEntity<Course> response = courseController.deleteCourse(CourseId);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteCourseNotFound() throws IOException { // deleteCourse may throw IOException
        // Setup
        int CourseId = 99;
        // when deleteCourse is called return false, simulating failed deletion
        when(mockCourseDAO.deleteCourse(CourseId)).thenReturn(false);

        // Invoke
        ResponseEntity<Course> response = courseController.deleteCourse(CourseId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteCourseHandleException() throws IOException { // deleteCourse may throw IOException
        // Setup
        int CourseId = 99;
        // When deleteCourse is called on the Mock Course DAO, throw an IOException
        doThrow(new IOException()).when(mockCourseDAO).deleteCourse(CourseId);

        // Invoke
        ResponseEntity<Course> response = courseController.deleteCourse(CourseId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testSearchCourses() throws IOException {
        // Setup
        String searchString = "ca";
        Course[] courses = new Course[2];
        courses[0] = course;
        courses[1] = new Course(100, null, "Course 100", 12, "Course 100 Description", 0, new HashSet<>(),
                new ArrayList<>());
        // When findCourses is called with the search string, return the two
        // courses above
        when(mockCourseDAO.findCourses(searchString)).thenReturn(courses);

        // Invoke
        ResponseEntity<Course[]> response = courseController.searchCourses(searchString);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courses, response.getBody());

    }

    @Test
    public void testSearchCoursesHandleException() throws IOException {
        // Setup
        String searchString = "na";
        // When createCourse is called on the Mock Course DAO, throw an IOExeption
        doThrow(new IOException()).when(mockCourseDAO).findCourses(searchString);

        // Invoke
        ResponseEntity<Course[]> response = courseController.searchCourses(searchString);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
