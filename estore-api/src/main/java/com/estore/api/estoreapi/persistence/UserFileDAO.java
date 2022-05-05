package com.estore.api.estoreapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserFileDAO implements UserDAO {

    private static final Logger LOG = Logger.getLogger(User.class.getName());

    private Map<String, User> users;
    private ObjectMapper objectMapper;
    private String filename;

    private CourseDAO courseDAO;

    public UserFileDAO(@Value("${users.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load();
    }

    private boolean save() throws IOException {
        User[] usersArray = getUsersArray();

        // Serializes the Java Objects to JSON objects into the file
        // writeValue will thrown an IOException if there is an issue
        // with the file or reading from the file
        objectMapper.writeValue(new File(filename), usersArray);
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
        users = new TreeMap<>();

        // Deserializes the JSON objects from the file into an array of users
        // readValue will throw an IOException if there's an issue with the file
        // or reading from the file
        User[] userArray = objectMapper.readValue(new File(filename), User[].class);

        // Add each Course to the tree map and keep track of the greatest id
        for (User user : userArray) {
            users.put(user.getUserName(), user);
        }
        // Make the next id one greater than the maximum from the file
        return true;
    }

    /**
     * Gets array of all users.
     * 
     * @return array of all users.
     */
    private User[] getUsersArray() {
        ArrayList<User> usersList = new ArrayList<>();

        for (User user : users.values()) {
            usersList.add(user);
        }

        User[] usersArray = new User[usersList.size()];
        usersList.toArray(usersArray);
        return usersArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(String userName) {
        synchronized (users) {
            return users.get(userName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user) throws IOException {
        synchronized (users) {
            if (!users.containsKey(user.getUserName())) {
                users.put(user.getUserName(), user);
                save();
                return user;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Integer> getUserCourse(String userName) {

        synchronized (users) {
            if (users.containsKey(userName)) {
                return users.get(userName).getCourses();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateUserCourses(String userName, Set<Integer> courses) throws IOException {

        synchronized (users) {
            if (users.containsKey(userName)) {
                for (Integer id : courses) {
                    Course course = this.courseDAO.getCourse(id);
                    int enrolled = course.getStudentsEnrolled();
                    enrolled += 1;
                    course.setStudentsEnrolled(enrolled);
                    this.courseDAO.updateCourse(course);
                }
                users.get(userName).updateUserCourse(courses);
                save();
                return users.get(userName);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User[] getAllUsers() {
        synchronized (users) {
            return getUsersArray();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Course[] getUserShoppingCart(String userName) {

        synchronized (users) {
            if (users.containsKey(userName)) {
                return users.get(userName).getShoppingCart(courseDAO);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateUserShoppingCart(String userName, Set<Integer> courses) throws IOException {

        synchronized (users) {
            if (users.containsKey(userName)) {
                users.get(userName).updateShoppingCart(courses);
                save();
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCourseDAO(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateUser(User user) throws IOException {
        synchronized (users) {
            if (users.containsKey(user.getUserName()) == false)
                return null; // User does not exist

            users.put(user.getUserName(), user);
            save(); // may throw an IOException
            return user;
        }
    }

}