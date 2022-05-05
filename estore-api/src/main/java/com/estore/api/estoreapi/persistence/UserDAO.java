package com.estore.api.estoreapi.persistence;

import java.io.IOException;
import java.util.Set;

import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;

public interface UserDAO {

    void setCourseDAO(CourseDAO courseDAO);

    /**
     * Retrieves a {@linkplain User user} with the given userName
     *
     * @param userName The id of the {@link User userName} to get
     *
     * @return a {@link User userName} object with the matching userName
     *         <br>
     *         null if no {@link User userName} with a matching userName is found
     *
     * @throws IOException if an issue with underlying storage
     */
    User getUser(String userName);

    /**
     * Retrieves all {@linkplain User users}
     *
     * @return An array of {@link User user} objects, may be empty
     *
     * @throws IOException if an issue with underlying storage
     */
    // User[] getUsers() throws IOException;

    /**
     * Creates and saves a {@linkplain User user}
     *
     * @param user {@linkplain User user} object to be created and saved
     *             <br>
     *             The id of the hero object is ignored and a new uniqe userName is
     *             assigned
     *
     * @return new {@link User user} if successful, false otherwise
     *
     * @throws IOException if an issue with underlying storage
     */
    User createUser(User user) throws IOException;

    /**
     * Gets list of users registered course ids
     * 
     * @param userName The id of the {@link User userName} to get
     * @return A set of {@link Integers} that represent courses ids of courses the
     *         user bought.
     * @throws IOException
     */

    Set<Integer> getUserCourse(String userName) throws IOException;

    /**
     * Updates user registered courses
     * 
     * @param userName The id of the {@link User userName} to get
     * @param courses  The list of courses bought that need to be added to the user
     *                 courses array
     * @return the updated {@link User user} if successful, false otherwise
     * @throws IOException
     */
    User updateUserCourses(String userName, Set<Integer> courses) throws IOException;

    /**
     * Gets the users shopping cart
     * 
     * @param userName The id of the {@link User userName} to get
     * @return a list {@linkplain Course courses} that are currently in the user
     *         shopping or a
     *         empty list.
     * @throws IOException
     */
    Course[] getUserShoppingCart(String userName) throws IOException;

    /**
     * Updates user shopping cart when a course is added or deleted.
     * 
     * @param userName The id of the {@link User userName} to get
     * @param courses  The list of courses bought that need to be added to the user
     *                 courses array
     * @return true if the cart was succesfully updated, false otherwise.
     * @throws IOException
     */

    boolean updateUserShoppingCart(String userName, Set<Integer> courses) throws IOException;

    /**
     * Updates and saves a {@linkplain User user}
     *
     * @param {@link User user} object to be updated and saved
     * @return updated {@link User user} if successful, null if
     *         {@link User user} could not be found
     *
     * @throws IOException if underlying storage cannot be accessed
     */
    User updateUser(User user) throws IOException;

    /**
     * Gets all {@linkplain User users}
     * 
     * @return An array of {@link User user} objects, may be empty
     */
    User[] getAllUsers();

}
