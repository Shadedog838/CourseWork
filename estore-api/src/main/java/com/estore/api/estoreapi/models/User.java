package com.estore.api.estoreapi.models;

import com.estore.api.estoreapi.helper.DataHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    private static final Logger LOG = Logger.getLogger(User.class.getName());
    public static final String ADMIN_USER_NAME = "Admin";
    @Id
    private String id;
    @NotBlank
    @Size(max=20)
    private String userName;
    @NotBlank
    @Size(max=50)
    @Email
    private String email;
    @NotBlank
    @Size(max=120)
    private String password;
    private Image image;
    private Set<String> courses;
    private Set<String> shoppingCart;
    private boolean banned;

    public User() {

    }
    /**
     * test constructor
     *
     * @param userName
     */
    // public User(String userName, Set<Integer> courses) {
    //     this.userName = userName;
    //     this.courses = courses;
    //     shoppingCart = new HashSet<>();
    //     banned = false;
    //     this.email = "";
    // }

    /**
     * another test constructor
     *
     * @param userName
     */
    // public User(String userName) {
    //     this.userName = userName;
    //     this.courses = new HashSet<>();
    //     shoppingCart = new HashSet<>();
    //     banned = false;
    //     this.email = "";
    // }

    public User(String userName, String password,
            String email, Image image, Set<String> courses,
            Set<String> shoppingCart,
            boolean banned) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.image = image;
        this.courses = courses;
        this.shoppingCart = shoppingCart;
        this.banned = banned;
    }

    public String getId() {
        return this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Set<String> getCourses() {
        return this.courses;
    }

    public void setCourses(Set<String> courses) {
        this.courses = courses;
    }

    public void setUsersEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void addRegisteredCourses(Set<String> courseIDs) {
        courses.addAll(courseIDs);
    }

    public void updateUserCourse(Set<String> newCourse) {
        addRegisteredCourses(newCourse);
        shoppingCart.clear();
    }

    public List<Course> getShoppingCart(DataHelper dataHelper) {
        Course[] cartCourses = new Course[shoppingCart.size()];
        int i = 0;
        for (String courseID : shoppingCart) {
            cartCourses[i++] = dataHelper.getCourse(courseID);
        }
        List<Course> cart = Arrays.asList(cartCourses);
        return cart;
    }

    public Set<String> getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppinCart(Set<String> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public void addCourseToShoppingCartByID(String courseID) {
        shoppingCart.add(courseID);
    }

    public void addCoursesToShoppingCartByID(Set<String> courseIDs) {
        shoppingCart.addAll(courseIDs);
    }

    public void updateShoppingCart(Set<String> newCart) {
        shoppingCart.clear();
        addCoursesToShoppingCartByID(newCart);
    }

    public List<Course> getUserCourses(DataHelper dataHelper) {
        List<Course> userCourses = new ArrayList<>();
        LOG.info("arrary " + userCourses);
        for (String courseId : courses) {
            userCourses.add(dataHelper.getCourse(courseId));
        }
        return userCourses;

    }

    @Override
    public String toString() {
        return "User {" +
                "id:" + id +
                ", userName:" + userName +
                ", password:" + password +
                ", shoppingCart:" + shoppingCart +
                ", courses:" + courses +
                ", email: " + email +
                "}";
    }
}
