package com.estore.api.estoreapi.controller;

import com.estore.api.estoreapi.controller.requests.AuthenticatedRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.estore.api.estoreapi.model.Course;
import com.estore.api.estoreapi.model.User;
import com.estore.api.estoreapi.persistence.CourseDAO;
import com.estore.api.estoreapi.persistence.UserDAO;

@RestController
@RequestMapping("users")
public class UserController {

    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    private final UserDAO userDao;
    private final CourseDAO courseDAO;

    public UserController(UserDAO userDao, CourseDAO courseDAO) {
        this.userDao = userDao;
        this.courseDAO = courseDAO;
        this.userDao.setCourseDAO(courseDAO);
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        LOG.info("POST users/register " + user);

        try {
            return userDao.createUser(user) == null ? new ResponseEntity<>(HttpStatus.CONFLICT)
                    : new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userName}")
    public ResponseEntity<User> getUser(@PathVariable String userName) {
        LOG.info("GET /users/" + userName);
        User user = userDao.getUser(userName);
        if (user != null && user.isBanned()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{username}/recommended/{amt}")
    public ResponseEntity<Course[]> getRecommendedCourses(@PathVariable String username, @PathVariable int amt) {
        LOG.info("GET " + username + "/recommended/" + amt);

        User user = userDao.getUser(username);
        if (user != null && user.isBanned()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (user != null) {
            Course[] courses = courseDAO.getRecommendedCoursesForUser(user);
            return new ResponseEntity<Course[]>(Arrays.copyOfRange(courses, 0, Math.min(amt, courses.length)),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Gets a list of all registered users.
     *
     * Only admins are allowed to use this endpoint. Ideally, it would use the
     * session token to check if the user is
     * an admin; however, since we're not doing sessions, we'll just check if the
     * user is an admin with a request param.
     * 
     * @param userName the name of the user making the request
     * @return a list of all registered users, or UNAUTHORIZED if the user is not an
     *         admin
     */
    @GetMapping("")
    public ResponseEntity<User[]> getAllUsers(@RequestParam String userName) {
        LOG.info("GET /users?userName=" + userName);
        // only admins can see all the users
        if (!userName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userName}/cart")
    public ResponseEntity<Course[]> getUserShoppingCart(@PathVariable String userName) {
        LOG.info("GET /users/ " + userName + "/cart");
        User user = userDao.getUser(userName);
        if (user != null) {
            Course[] cartCourses = user.getShoppingCart(courseDAO);
            return new ResponseEntity<Course[]>(cartCourses, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> getUserAccount(@RequestBody User user) {
        LOG.info("POST users/login " + user);
        User userObj = userDao.getUser(user.getUserName());
        if (userObj != null && userObj.isBanned()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return (userObj == null) ? new ResponseEntity<>(HttpStatus.CONFLICT)
                : new ResponseEntity<>(userObj, HttpStatus.OK);
    }

    /**
     * Bans a user
     *
     * @param userName      the user to ban
     * @param requesterName the user requesting the ban
     * @return the user that was banned, or a FORBIDDEN if the requester is not an
     *         admin
     */
    @PostMapping("/{userName}/ban")
    public ResponseEntity<User> banUser(@PathVariable String userName, @RequestBody String requesterName) {
        LOG.info("POST users/" + userName + "/ban");
        // only allow admins
        if (!requesterName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User user = userDao.getUser(userName);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setBanned(true);
        return updateUser(new AuthenticatedRequest<>(user, requesterName));
    }

    /**
     * Unbans a user
     *
     * @param userName      the user to unban
     * @param requesterName the user requesting the unban
     * @return the user that was unbanned, or a FORBIDDEN if the requester is not an
     *         admin
     */
    @PostMapping("/{userName}/unban")
    public ResponseEntity<User> unbanUser(@PathVariable String userName, @RequestBody String requesterName) {
        LOG.info("POST users/" + userName + "/unban");
        // only allow admins
        if (!requesterName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User user = userDao.getUser(userName);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setBanned(false);
        return updateUser(new AuthenticatedRequest<>(user, requesterName));
    }

    /**
     * Updates a user
     *
     * Users may only update their own information, and only if they are not banned.
     * Admins may update any user.
     * 
     * @param request the user to update and the name of the user making the request
     * @return the updated user, or a FORBIDDEN response if the user is banned,
     *         editing a user that isn't themselves, or
     *         not an admin
     */
    @PutMapping("")
    public ResponseEntity<User> updateUser(@RequestBody AuthenticatedRequest<User> request) {
        String userName = request.getUserName();
        User user = request.getData();
        LOG.info("PUT /users " + user);

        // only allow admins and unbanned users to update their own information
        if (!userName.equalsIgnoreCase(User.ADMIN_USER_NAME)
                && (user.isBanned() || !userName.equals(user.getUserName()))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            return userDao.updateUser(user) == null ? new ResponseEntity<>(HttpStatus.CONFLICT)
                    : new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userName}/courses")
    public ResponseEntity<Course[]> getUserCourses(@PathVariable String userName) {
        LOG.info("Get /user/" + userName + "/courses");
        User userObj = userDao.getUser(userName);
        if (userObj != null && userObj.isBanned()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return userObj == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<Course[]>(userObj.getUserCourses(courseDAO), HttpStatus.OK);
    }

    @PutMapping("/checkout")
    public ResponseEntity<User> updateUserCourses(@RequestBody User user) {
        LOG.info("Put /users/checkout " + user.toString());
        try {
            User userObj = userDao.updateUserCourses(user.getUserName(), user.getCourses());
            return userObj == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                    : new ResponseEntity<User>(userObj, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
