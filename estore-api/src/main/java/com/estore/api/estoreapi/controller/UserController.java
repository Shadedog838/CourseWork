package com.estore.api.estoreapi.controller;

import com.estore.api.estoreapi.controller.requests.AuthenticatedRequest;
import com.estore.api.estoreapi.helper.DataHelper;
import com.estore.api.estoreapi.models.Course;
import com.estore.api.estoreapi.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.estore.api.estoreapi.repository.CourseRepository;
import com.estore.api.estoreapi.repository.UserRepository;

@RestController
@RequestMapping("users")
public class UserController {

    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    @Autowired
    UserRepository userRepository;
    private final CourseRepository courseRepository;
    // private final UserDAO userDao;
    private final DataHelper dataHelper;

    public UserController(DataHelper dataHelper, CourseRepository courseRepository) {
        this.dataHelper = dataHelper;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        LOG.info("POST users/register " + user);

        try {
            Optional<User> userUsername = userRepository.findByUserName(user.getUserName());
            Optional<User> userPassword = userRepository.findByPassword(user.getPassword());
            Optional<User> userEmail = userRepository.findByEmail(user.getEmail());
            LOG.info("userUsername: " + userUsername);
            if (userUsername.isPresent() || userPassword.isPresent() || userEmail.isPresent()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            User _user = userRepository.save(new User(user.getUserName(), user.getPassword(), user.getEmail(),
                    user.getImage(), user.getCourses(), user.getCourses(), user.isBanned()));
            return new ResponseEntity<>(_user, HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        LOG.info("GET /users/" + id);
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent() && !userData.get().isBanned()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else if (userData.isPresent() && !userData.get().isBanned()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/recommended/{amt}")
    public ResponseEntity<List<Course>> getRecommendedCourses(@PathVariable("id") String id, @PathVariable int amt) {
        LOG.info("GET " + id + "/recommended/" + amt);

        Optional<User> userData = userRepository.findById(id);
        User user = userData.get();
        if (user != null && user.isBanned()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (user != null) {
            Course[] coursesRes = dataHelper.getRecommendedCoursesForUser(user);
            Course[] courses = Arrays.copyOfRange(coursesRes, 0, Math.min(amt, coursesRes.length));
            List<Course> courseArr = Arrays.asList(courses);
            return new ResponseEntity<>(courseArr, HttpStatus.OK);
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
    public ResponseEntity<List<User>> getAllUsers(@RequestParam String id) {
        LOG.info("GET /users?id" + id);
        // only admins can see all the users
        User user = userRepository.findById(id).get();
        if (!user.getUserName().equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}/cart")
    public ResponseEntity<List<Course>> getUserShoppingCart(@PathVariable("id") String id) {
        LOG.info("GET /users/" + id + "/cart");
        Optional<User> userData = userRepository.findById(id);
        User user = userData.get();
        if (user != null) {
            List<Course> cartCourses = user.getShoppingCart(dataHelper);
            return new ResponseEntity<>(cartCourses, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> getUserAccount(@RequestBody User user) {
        LOG.info("POST users/login " + user);
        Optional<User> userName = userRepository.findByUserName(user.getUserName());
        Optional<User> userPassword = userRepository.findByPassword(user.getPassword());
        if (!userName.isPresent() || !userPassword.isPresent()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User userObj = userName.get();
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
    @PostMapping("/{id}/ban")
    public ResponseEntity<User> banUser(@PathVariable("id") String id, @RequestBody String requesterId) {
        LOG.info("POST users/" + id + "/ban");
        // only allow admins
        String requesterName = userRepository.findById(requesterId).get().getUserName();
        if (!requesterName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<User> userData = userRepository.findById(id);
        User user = userData.get();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setBanned(true);
        return updateUser(new AuthenticatedRequest<User>(user, requesterId));
    }

    /**
     * Unbans a user
     *
     * @param userName      the user to unban
     * @param requesterName the user requesting the unban
     * @return the user that was unbanned, or a FORBIDDEN if the requester is not an
     *         admin
     */
    @PostMapping("/{id}/unban")
    public ResponseEntity<User> unbanUser(@PathVariable("id") String id, @RequestBody String requesterId) {
        LOG.info("POST users/" + id + "/unban");
        // only allow admins
        String requesterName = userRepository.findById(requesterId).get().getUserName();
        if (!requesterName.equalsIgnoreCase(User.ADMIN_USER_NAME)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<User> userData = userRepository.findById(id);
        User user = userData.get();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setBanned(false);
        return updateUser(new AuthenticatedRequest<User>(user, requesterId));
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
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody AuthenticatedRequest<User> request) {
        LOG.info("PUT /users " + request.getData());
        // only allow admins and unbanned users to update their own information
        User user = request.getData();
        String userId = request.getUserId();
        String userName = userRepository.findById(userId).get().getUserName();
        Optional<User> userData = userRepository.findById(user.getId());

        if (userData.isPresent()) {
            if (!userName.equalsIgnoreCase(User.ADMIN_USER_NAME) && userData.get().isBanned()) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            User _user = userData.get();
            _user.setUserName(user.getUserName());
            _user.setPassword(user.getPassword());
            _user.setUsersEmail(user.getEmail());
            _user.setImage(user.getImage());
            _user.setCourses(user.getCourses());
            _user.setShoppinCart(user.getShoppingCart());
            _user.setBanned(user.isBanned());
            LOG.info("PUT /users " + _user);
            return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getUserCourses(@PathVariable("id") String id) {
        LOG.info("Get /user/" + id + "/courses");
        Optional<User> userData = userRepository.findById(id);
        User userObj = userData.get();
        if (userObj != null && userObj.isBanned()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return userObj == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(userObj.getUserCourses(dataHelper), HttpStatus.OK);
    }

    @PutMapping("/checkout")
    public ResponseEntity<User> updateUserCourses(@RequestBody User user) {
        LOG.info("Put /users/checkout " + user.toString());
        try {
            User userObj = dataHelper.updateUserCourses(user.getId(), user.getCourses());
            LOG.info("userObj: " + userObj.toString());
            return userObj == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                    : new ResponseEntity<User>(userRepository.save(userObj), HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
