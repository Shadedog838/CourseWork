package com.estore.api.estoreapi.controller;

import com.estore.api.estoreapi.controller.requests.AuthenticatedRequest;
import com.estore.api.estoreapi.helper.DataHelper;
import com.estore.api.estoreapi.models.Course;
import com.estore.api.estoreapi.models.User;
import com.estore.api.estoreapi.repository.CourseRepository;
import com.estore.api.estoreapi.repository.UserRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the REST API requests for the Course resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST
 * API
 * method handler to the Spring framework
 */

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("courses")
public class CourseController {
    private static final Logger LOG = Logger.getLogger(CourseController.class.getName());
    @Autowired
    CourseRepository courseRepository;
    private final DataHelper dataHelper;
    UserRepository userRepository;

    public CourseController(DataHelper dataHelper, UserRepository userRepository) {
        this.dataHelper = dataHelper;
        this.userRepository = userRepository;
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
        LOG.info("POST /courses " + authenticatedRequest.getUserId());
        Course course = authenticatedRequest.getData();
        String userId = authenticatedRequest.getUserId();
        String userName = userRepository.findById(userId).get().getUserName();
        if (!userName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            Course _course = courseRepository.save(new Course(course.getImage(), course.getTitle(),
             course.getPrice(), course.getDescription(), course.getStudentsEnrolled(), course.getTags(), course.getContent()));
             return new ResponseEntity<Course>(_course, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String text) {
        LOG.info("GET /courses/?text=" + text);

        try {
            List<Course> courses = new ArrayList<Course>();
            Course[] coursesArr = dataHelper.getCoursesArray(text, courseRepository.findAll());
            courses = Arrays.asList(coursesArr);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
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
    public ResponseEntity<Course> getCourse(@PathVariable("id") String id) {
        LOG.info("GET /courses/" + id);
        Optional<Course> courseData = courseRepository.findById(id);
        if (courseData.isPresent()) {
            return new ResponseEntity<>(courseData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
    public ResponseEntity<List<Course>> getCourses() {
        LOG.info("GET /courses");

        try {
            List<Course> courses = new ArrayList<Course>();
            courseRepository.findAll().forEach(courses::add);
            Course[] coursesArr = dataHelper.getCoursesArray(null, courses);
            courses = Arrays.asList(coursesArr);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
        String userId = authenticatedRequest.getUserId();
        String userName = userRepository.findById(userId).get().getUserName();
        if (!userName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<Course> courseData = courseRepository.findById(course.getId());
        if (courseData.isPresent()) {
            Course _course = courseData.get();
            _course.setImage(course.getImage());
            _course.setTitle(course.getTitle());
            _course.setPrice(course.getPrice());
            _course.setDescription(course.getDescription());
            _course.setStudentsEnrolled(course.getStudentsEnrolled());
            _course.setTags(course.getTags());
            _course.setContent(course.getContent());
            return new ResponseEntity<>(courseRepository.save(_course), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<Course> deleteCourse(@PathVariable("id") String id) {
        LOG.info("DELETE/COURSES/" + id);
        try {
            if (dataHelper.deleteCourse(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch ( Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}