package com.estore.api.estoreapi.persistence;

import java.io.IOException;
import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;

public interface CourseDAO {
    /**
     * Retrieves all {@linkplain Course courses}
     *
     * @return An array of {@link Course course} objects, may be empty
     *
     * @throws IOException if an issue with underlying storage
     */
    Course[] getCourses() throws IOException;

    /**
     * Finds all {@linkplain Course courses} whose name contains the given text
     *
     * @param containsText The text to match against
     *
     * @return An array of {@link Course courses} whose nemes contains the given
     *         text, may be empty
     *
     * @throws IOException if an issue with underlying storage
     */
    Course[] findCourses(String containsText) throws IOException;

    /**
     * Retrieves a {@linkplain Course course} with the given id
     *
     * @param id The id of the {@link Course course} to get
     *
     * @return a {@link Course course} object with the matching id
     *         <br>
     *         null if no {@link Course course} with a matching id is found
     *
     * @throws IOException if an issue with underlying storage
     */
    Course getCourse(int id);

    /**
     * Creates and saves a {@linkplain Course course}
     *
     * @param hero {@linkplain Course course} object to be created and saved
     *             <br>
     *             The id of the hero object is ignored and a new uniqe id is
     *             assigned
     *
     * @return new {@link Course course} if successful, false otherwise
     *
     * @throws IOException if an issue with underlying storage
     */
    Course createCourse(Course course) throws IOException;

    /**
     * Updates and saves a {@linkplain Course course}
     *
     * @param {@link Course course} object to be updated and saved
     *
     * @return updated {@link Course course} if successful, null if
     *         {@link Course course} could not be found
     *
     * @throws IOException if underlying storage cannot be accessed
     */
    Course updateCourse(Course course) throws IOException;

    /**
     * Deletes a {@linkplain Course course} with the given id
     *
     * @param id The id of the {@link Course course}
     *
     * @return true if the {@link Course course} was deleted
     *         <br>
     *         false if hero with the given id does not exist
     *
     * @throws IOException if underlying storage cannot be accessed
     */
    boolean deleteCourse(int id) throws IOException;

    /**
     * Gets array of {@link Course courses} that are recommended to the user.
     * 
     * @param user {@link User user} user object of user currently loged in.
     * @return an array {@linkplain Course courses} of recommended courses, may be
     *         empty.
     */
    public Course[] getRecommendedCoursesForUser(User user);

    void setUserDAO(UserDAO userDAO);
}
