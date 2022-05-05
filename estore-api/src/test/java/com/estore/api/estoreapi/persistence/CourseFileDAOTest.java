package com.estore.api.estoreapi.persistence;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Persistence-tier")
public class CourseFileDAOTest {
    CourseFileDAO courseFileDAO;
    Course[] testCourses;
    ObjectMapper mockObjectMapper1;
    ObjectMapper mockObjectMapper2;

    /**
     * Before each test, we will create and inject a Mock Object Mapper to
     * isolate the tests from the underlying file
     * 
     * @throws IOException
     */
    @BeforeEach
    public void setupCourseFileDAO() throws IOException {
        mockObjectMapper1 = mock(ObjectMapper.class);
        mockObjectMapper2 = mock(ObjectMapper.class);

        Set<String> testTags1 = new HashSet<String>();
        testTags1.add("math");
        testTags1.add("discrete");

        Set<String> testTags2 = new HashSet<String>();
        testTags2.add("math");
        testTags2.add("linear");

        Set<String> testTags3 = new HashSet<String>();
        testTags3.add("math");
        testTags3.add("statistics");

        testCourses = new Course[3];
        testCourses[0] = new Course(99, "Discrete Math", 9.99, "This is a course about discrete math", testTags1);
        testCourses[1] = new Course(100, "Linear Math", 8.99, "This is a course about linear math", testTags2);
        testCourses[2] = new Course(101, "Stats Math", 10.99, "This is a course about stats math", testTags3);

        User[] testUsers = new User[1];
        testUsers[0] = new User("bobie12");

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the course array above
        when(mockObjectMapper1
                .readValue(new File("doesnt_matter1.txt"), Course[].class))
                .thenReturn(testCourses);
        when(mockObjectMapper2
                .readValue(new File("doesnt_matter2.txt"), User[].class))
                .thenReturn(testUsers);
        courseFileDAO = new CourseFileDAO("doesnt_matter1.txt", mockObjectMapper1);
        UserDAO mockUserDao = new UserFileDAO("doesnt_matter2.txt", mockObjectMapper2);
        courseFileDAO.setUserDAO(mockUserDao);
        mockUserDao.setCourseDAO(courseFileDAO);

    }

    @Test
    public void testGetCourses() {
        // Invoke
        Course[] courses = courseFileDAO.getCourses();

        // Analyze
        assertEquals(courses.length, testCourses.length);
        for (int i = 0; i < testCourses.length; ++i)
            assertEquals(courses[i], testCourses[i]);
    }

    @Test
    public void testFindByTitleCourses() {
        // Invoke
        Course[] courses = courseFileDAO.findCourses("Ma");

        // Analyze
        assertEquals(courses.length, 3);
        assertEquals(courses[0], testCourses[0]);
        assertEquals(courses[1], testCourses[1]);
        assertEquals(courses[2], testCourses[2]);
    }

    @Test
    public void testFindByDescriptionCourses() {
        // Invoke
        Course[] courses = courseFileDAO.findCourses("This is a course about");

        // Analyze
        assertEquals(courses.length, 3);
        assertEquals(courses[0], testCourses[0]);
        assertEquals(courses[1], testCourses[1]);
        assertEquals(courses[2], testCourses[2]);
    }

    @Test
    public void testFindCoursesFail() {
        // Invoke
        Course[] courses = courseFileDAO.findCourses("bkvdjsbvb");

        // Analyze
        assertEquals(courses.length, 0);
    }

    @Test
    public void testFindByPriceCourses() {
        // Invoke
        Course[] courses = courseFileDAO.findCourses("9.99");

        // Analyze
        assertEquals(courses.length, 2);
        assertEquals(courses[0], testCourses[0]);
        assertEquals(courses[1], testCourses[1]);
    }

    @Test
    public void testGetCourse() {
        // Invoke
        Course course = courseFileDAO.getCourse(99);

        // Analzye
        assertEquals(course, testCourses[0]);
    }

    @Test
    public void testDeleteCourse() {
        // Invoke
        boolean result = assertDoesNotThrow(() -> courseFileDAO.deleteCourse(99),
                "Unexpected exception thrown");

        // Analzye
        assertEquals(true, result);
        // We check the internal tree map size against the length
        // of the test courses array - 1 (because of the delete)
        // Because courses attribute of CourseFileDAO is package private
        // we can access it directly
        assertEquals(courseFileDAO.courses.size(), testCourses.length - 1);
    }

    @Test
    public void testCreateCourse() {
        // Setup
        Course course = new Course(102, "Excel", 30.99, "This is a course about Excel");

        // Invoke
        Course result = assertDoesNotThrow(() -> courseFileDAO.createCourse(course),
                "Unexpected exception thrown");

        // Analyze
        assertNotNull(result);
        Course actual = courseFileDAO.getCourse(course.getId());
        assertEquals(actual.getId(), course.getId());
        assertEquals(actual.getTitle(), course.getTitle());
        assertEquals(actual.getPrice(), course.getPrice());
    }

    @Test
    public void testUpdateCourse() {
        // Setup
        Course course = new Course(99, "Linear Math", 8.99, "This is a course about linear math");

        // Invoke
        Course result = assertDoesNotThrow(() -> courseFileDAO.updateCourse(course),
                "Unexpected exception thrown");

        // Analyze
        assertNotNull(result);
        Course actual = courseFileDAO.getCourse(course.getId());
        assertEquals(actual, course);
    }

    @Test
    public void testGetRecommendedCoursesForUser() {
        HashSet<Integer> testRegisteredCourses = new HashSet<>();
        testRegisteredCourses.add(testCourses[0].getId());
        User testUser = new User("Bob12", testRegisteredCourses);

        Course[] recommendedResult = courseFileDAO.getRecommendedCoursesForUser(testUser);

        assertEquals(testCourses[2], recommendedResult[0]);
        assertEquals(testCourses[1], recommendedResult[1]);
    }

    @Test
    public void testSaveException() throws IOException {
        doThrow(new IOException())
                .when(mockObjectMapper1)
                .writeValue(any(File.class), any(Course[].class));

        Course course = new Course(102, "Stats", 100.00, "This is a course about stats");

        assertThrows(IOException.class,
                () -> courseFileDAO.createCourse(course),
                "IOException not thrown");
    }

    @Test
    public void testGetCourseNotFound() {
        // Invoke
        Course course = courseFileDAO.getCourse(98);

        // Analyze
        assertEquals(course, null);
    }

    @Test
    public void testDeleteCourseNotFound() {
        // Invoke
        boolean result = assertDoesNotThrow(() -> courseFileDAO.deleteCourse(98),
                "Unexpected exception thrown");

        // Analyze
        assertEquals(result, false);
        assertEquals(courseFileDAO.courses.size(), testCourses.length);
    }

    @Test
    public void testUpdateCourseNotFound() {
        // Setup
        Course course = new Course(98, "Circuits", 80.99, "This is a course about circuits");

        // Invoke
        Course result = assertDoesNotThrow(() -> courseFileDAO.updateCourse(course),
                "Unexpected exception thrown");

        // Analyze
        assertNull(result);
    }

    @Test
    public void testConstructorException() throws IOException {
        // Setup
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        // We want to simulate with a Mock Object Mapper that an
        // exception was raised during JSON object deseerialization
        // into Java objects
        // When the Mock Object Mapper readValue method is called
        // from the CourseFileDAO load method, an IOException is
        // raised
        doThrow(new IOException())
                .when(mockObjectMapper)
                .readValue(new File("doesnt_matter.txt"), Course[].class);

        // Invoke & Analyze
        assertThrows(IOException.class,
                () -> new CourseFileDAO("doesnt_matter.txt", mockObjectMapper),
                "IOException not thrown");
    }
}
