package com.estore.api.estoreapi.controller;

import com.estore.api.estoreapi.controller.requests.AuthenticatedRequest;
import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;
import com.estore.api.estoreapi.persistence.CourseDAO;
import com.estore.api.estoreapi.persistence.UserDAO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the REST API requests for the Course resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST
 * API
 * method handler to the Spring framework
 */

@RestController
@RequestMapping("courses")
public class CourseController {
    private static final Logger LOG = Logger.getLogger(CourseController.class.getName());
    private final CourseDAO courseDao;
    private final UserDAO userDAO;

    /**
     * Creates a REST API controller to reponds to requests
     *
     * @param courseDao The {@link CourseDAO Course Data Access Object} to perform
     *                  CRUD operations
     *                  <br>
     *                  This dependency is injected by the Spring Framework
     */
    public CourseController(UserDAO userDAO, CourseDAO courseDao) {
        this.courseDao = courseDao;
        this.userDAO = userDAO;
        this.courseDao.setUserDAO(userDAO);
    }

    /**
     * Creates a course with the provided data and responds with either the created
     * course or an error code
     *
     * @param authenticatedRequest course to create
     * @return {@code ResponseEntity} with the created {@link Course} and
     *         {@link HttpStatus#CREATED}, or an
     *         {@link HttpStatus#INTERNAL_SERVER_ERROR}
     */
    @PostMapping("")
    public ResponseEntity<Course> createCourse(@RequestBody AuthenticatedRequest<Course> authenticatedRequest) {
        LOG.info("POST /courses " + authenticatedRequest);
        Course course = authenticatedRequest.getData();
        String userName = authenticatedRequest.getUserName();

        if (!userName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // create course object and return the response
        // if the course object already exists, return a conflict
        try {
            return courseDao.createCourse(course) == null ? new ResponseEntity<>(HttpStatus.CONFLICT)
                    : new ResponseEntity<>(course, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for all {@linkplain Course courses} whose title
     * contains
     * the text in title
     *
     * @param title The title parameter which contains the text used to find the
     *              {@link Course courses}
     * @return ResponseEntity with array of {@link Course course} objects (may be
     *         empty) and
     *         HTTP status of OK<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     *         <p>
     *         Example: Find all courses that contain the text "ma"
     *         GET http://localhost:8080/courses/?title=ma
     */
    @GetMapping("/")
    public ResponseEntity<Course[]> searchCourses(@RequestParam String title) {
        LOG.info("GET /courses/?title=" + title);

        try {
            Course[] courses = courseDao.findCourses(title);

            return new ResponseEntity<Course[]>(courses, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for a {@linkplain Course course} for the given id
     *
     * @param id The id used to locate the {@link Course course}
     * @return ResponseEntity with {@link Course course} object and HTTP status of
     *         OK if found<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable int id) {
        LOG.info("GET /courses/" + id);
        Course course = courseDao.getCourse(id);
        return course != null ? new ResponseEntity<>(course, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Responds to the GET request for all {@linkplain Course courses}
     *
     * @return ResponseEntity with array of {@link Course course} objects (may be
     *         empty) and
     *         HTTP status of OK<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */

    @GetMapping("")
    public ResponseEntity<Course[]> getCourses() {
        LOG.info("GET /courses");

        try {
            Course[] courses = courseDao.getCourses();
            return new ResponseEntity<Course[]>(courses, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the {@linkplain Course course} with the provided {@linkplain Course
     * course} object, if it exists
     *
     * @param authenticatedRequest The {@link Course} to update
     *
     * @return ResponseEntity with updated {@link Course course} object and HTTP
     *         status of OK if updated<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("")
    public ResponseEntity<Course> updateCourse(@RequestBody AuthenticatedRequest<Course> authenticatedRequest) {
        LOG.info("PUT /course" + authenticatedRequest);
        Course course = authenticatedRequest.getData();
        String userName = authenticatedRequest.getUserName();
        if (!userName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            Course courseObj = courseDao.updateCourse(course);
            if (courseObj == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(courseObj, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    /**
     * Deletes a {@linkplain Course course} with the given id
     *
     * @param id The id of the {@link Course course} to deleted
     *
     * @return ResponseEntity HTTP status of OK if deleted<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Course> deleteCourse(@PathVariable int id) {
        LOG.info("DELETE/COURSES/" + id);
        try {
            if (courseDao.deleteCourse(id) == true) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}