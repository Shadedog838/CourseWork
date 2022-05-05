package com.estore.api.estoreapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.estore.api.estoreapi.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.ordering.OrderByName;
import com.estore.api.estoreapi.ordering.OrderByPopularity;
import com.estore.api.estoreapi.ordering.OrderByPrice;

/**
 * Implements the functionality for JSON file-based peristance for courses
 *
 * {@literal @}Component Spring annotation instantiates a single instance of
 * this
 * class and injects the instance into other classes as needed
 *
 * @author SWEN Faculty
 */
@Component
public class CourseFileDAO implements CourseDAO {
    private static final Logger LOG = Logger.getLogger(Course.class.getName());
    Map<Integer, Course> courses; // Provides a local cache of the Course objects
    // so that we don't need to read from the file
    // each time
    private ObjectMapper objectMapper; // Provides conversion between Course
                                       // objects and JSON text format written
                                       // to the file
    private static int nextId; // The next Id to assign to a new Course
    private String filename; // Filename to read from and write to
    private CourseDAO courseDAO;
    private UserDAO userDAO;

    /**
     * Creates a Course File Data Access Object
     *
     * @param filename     Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object serialization
     *                     and deserialization
     *
     * @throws IOException when file cannot be accessed or read from
     */
    public CourseFileDAO(@Value("${courses.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load(); // load the courses from the file
    }

    /**
     * Generates the next id for a new {@linkplain Course Course}
     *
     * @return The next id
     */
    private synchronized static int nextId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    public ArrayList<Course> getCoursesByPrice(Double price) {
        ArrayList<Course> courseArrayList = new ArrayList<>();

        for (Course course : courses.values()) {
            if (course.getPrice() <= price) {
                courseArrayList.add(course);
            }
        }
        return courseArrayList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course[] getRecommendedCoursesForUser(User user) {
        if (user == null) {
            return new Course[0];
        }

        Course[] allCourses = getCoursesArray(null);
        Set<Course> userCoursesSet = user.getCourses().stream().map(this::getCourse).collect(Collectors.toSet());
        HashMap<Course, Integer> courseTagHits = new HashMap<>();

        for (Course siteCourse : allCourses) {
            for (Course userCourse : userCoursesSet) {
                if (siteCourse != userCourse) {
                    for (String userCourseTag : userCourse.getTags()) {
                        if (siteCourse.getTags().contains(userCourseTag) && !userCoursesSet.contains(siteCourse)) {
                            Integer temp = courseTagHits.computeIfPresent(siteCourse, (c, i) -> i + 1);
                            courseTagHits.putIfAbsent(siteCourse, temp == null ? 0 : temp); // the most unreadable code
                                                                                            // i've ever written
                        }
                    }
                }
            }
        }

        // sort my how many tag hits there are
        List<Map.Entry<Course, Integer>> entryList = new ArrayList<>(courseTagHits.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<Course, Integer>>() {
            public int compare(Map.Entry<Course, Integer> o1,
                    Map.Entry<Course, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        }.thenComparing((c1, c2) -> {
            return c1.getKey().getStudentsEnrolled() - c2.getKey().getStudentsEnrolled();
        }));

        Course[] retArr = new Course[entryList.size()];
        for (int i = 0; i < entryList.size(); ++i) {
            retArr[i] = entryList.get(i).getKey();
        }
        return retArr;
    }

    /**
     * Generates an array of {@linkplain Course courses} from the tree map
     *
     * @return The array of {@link Course courses}, may be empty
     */
    private Course[] getCoursesArray() {
        return getCoursesArray(null);
    }

    /**
     * Generates an array of {@linkplain Course courses} from the tree map for any
     * {@linkplain Course courses} that contains the text specified by containsText
     * <br>
     * If containsText is null, the array contains all of the {@linkplain Course
     * courses}
     * in the tree map
     *
     * @return The array of {@link Course courses}, may be empty
     */
    private Course[] getCoursesArray(String containsText) { // if containsText == null, no filter
        ArrayList<Course> courseArrayList = new ArrayList<>();
        Boolean byPrice = false;

        for (Course course : courses.values()) {
            if (containsText == null || course.getTitle().toLowerCase().contains(containsText.toLowerCase())
                    || course.getDescription().toLowerCase().contains(containsText.toLowerCase())
                    || course.getTags().contains(containsText.toLowerCase())) {
                courseArrayList.add(course);
            } else {
                boolean numeric = true;
                Double num = 0.00;
                try {
                    num = Double.parseDouble(containsText);
                } catch (NumberFormatException e) {
                    numeric = false;
                }

                if (numeric) {
                    courseArrayList = getCoursesByPrice(num);
                    byPrice = true;
                    break;
                }
            }
        }

        if (byPrice) {
            Collections.sort(courseArrayList,
                    new OrderByPrice().thenComparing(new OrderByPopularity().thenComparing(new OrderByName())));
        } else {
            Collections.sort(courseArrayList, new OrderByPopularity().thenComparing(new OrderByName()));
        }
        Course[] courseArray = new Course[courseArrayList.size()];
        courseArrayList.toArray(courseArray);
        return courseArray;
    }

    /**
     * Saves the {@linkplain Course courses} from the map into the file as an array
     * of JSON objects
     *
     * @return true if the {@link Course courses} were written successfully
     *
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        Course[] courseArray = getCoursesArray();

        // Serializes the Java Objects to JSON objects into the file
        // writeValue will thrown an IOException if there is an issue
        // with the file or reading from the file
        objectMapper.writeValue(new File(filename), courseArray);
        return true;
    }

    /**
     * Loads {@linkplain Course courses} from the JSON file into the map
     * <br>
     * Also sets next id to one more than the greatest id found in the file
     *
     * @return true if the file was read successfully
     *
     * @throws IOException when file cannot be accessed or read from
     */
    private boolean load() throws IOException {
        courses = new TreeMap<>();
        nextId = 0;

        // Deserializes the JSON objects from the file into an array of courses
        // readValue will throw an IOException if there's an issue with the file
        // or reading from the file
        Course[] courseArray = objectMapper.readValue(new File(filename), Course[].class);

        // Add each Course to the tree map and keep track of the greatest id
        for (Course course : courseArray) {
            courses.put(course.getId(), course);
            if (course.getId() > nextId)
                nextId = course.getId();
        }
        // Make the next id one greater than the maximum from the file
        ++nextId;
        return true;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Course[] getCourses() {
        synchronized (courses) {
            return getCoursesArray();
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Course[] findCourses(String containsText) {
        synchronized (courses) {
            return getCoursesArray(containsText);
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Course getCourse(int id) {
        synchronized (courses) {
            return courses.get(id);
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Course createCourse(Course course) throws IOException {
        synchronized (courses) {
            // We create a new Course object because the id field is immutable
            // and we need to assign the next unique id
            Course newCourse = new Course(nextId(), course.getImage(), course.getTitle(), course.getPrice(),
                    course.getDescription(),
                    course.getStudentsEnrolled(), course.getTags(), course.getContent());
            courses.put(newCourse.getId(), newCourse);
            save(); // may throw an IOException
            return newCourse;
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Course updateCourse(Course course) throws IOException {
        synchronized (courses) {
            if (!courses.containsKey(course.getId()))
                return null; // Course does not exist

            courses.put(course.getId(), course);
            save(); // may throw an IOException
            return course;
        }
    }

    @Override
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public boolean deleteCourse(int id) throws IOException {
        User[] users = (User[]) userDAO.getAllUsers();
        synchronized (courses) {
            if (courses.containsKey(id)) {
                for (User user : users) {
                    Set<Integer> cart = user.getShoppingCart();
                    Set<Integer> registerdCourses = user.getCoursesBy();
                    cart.remove(id);
                    registerdCourses.remove(id);
                    userDAO.updateUser(user);
                }
                courses.remove(id);
                return save();
            } else
                return false;
        }
    }
}
