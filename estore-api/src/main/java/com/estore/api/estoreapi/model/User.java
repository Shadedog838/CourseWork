package com.estore.api.estoreapi.model;

import com.estore.api.estoreapi.persistence.CourseDAO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class User {
    public static final String ADMIN_USER_NAME = "Admin";
    @JsonProperty("userName")
    private final String userName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
    @JsonProperty("address")
    private String address;
    @JsonProperty("courses")
    private Set<Integer> courses;
    @JsonProperty("shoppingCart")
    private Set<Integer> shoppingCart;
    @JsonProperty("banned")
    private boolean banned;

    /**
     * test constructor
     *
     * @param userName
     */
    public User(String userName, Set<Integer> courses) {
        this.userName = userName;
        this.courses = courses;
        shoppingCart = new HashSet<>();
        banned = false;
        this.name = userName;
        this.address = "";
        this.email = "";
    }

    /**
     * another test constructor
     *
     * @param userName
     */
    public User(String userName) {
        this.userName = userName;
        this.courses = new HashSet<>();
        shoppingCart = new HashSet<>();
        banned = false;
        this.name = userName;
        this.address = "";
        this.email = "";
    }

    @JsonCreator
    public User(@JsonProperty("userName") String userName,
            @JsonProperty("courses") Set<Integer> courses,
            @JsonProperty("shoppingCart") Set<Integer> shoppingCart,
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("address") String address,
            @JsonProperty("banned") boolean banned) {
        this.userName = userName;
        this.courses = courses;
        this.shoppingCart = shoppingCart;
        this.name = name;
        this.address = address;
        this.email = email;
        this.banned = banned;
    }

    public String getUserName() {
        return this.userName;
    }

    public Set<Integer> getCourses() {
        return this.courses;
    }

    public void setUsersName(String name) {
        this.name = name;
    }

    public void setUsersEmail(String email) {
        this.email = email;
    }

    public void setUsersAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void addRegisteredCourses(Set<Integer> courseIDs) {
        courses.addAll(courseIDs);
    }

    public void updateUserCourse(Set<Integer> newCourse) {
        addRegisteredCourses(newCourse);
        shoppingCart.clear();
    }

    public Course[] getShoppingCart(CourseDAO courseDAO) {
        Course[] cartCourses = new Course[shoppingCart.size()];
        int i = 0;
        for (int courseID : shoppingCart) {
            cartCourses[i++] = courseDAO.getCourse(courseID);
        }
        return cartCourses;
    }

    public Set<Integer> getShoppingCart() {
        return shoppingCart;
    }

    public void addCourseToShoppingCartByID(int courseID) {
        shoppingCart.add(courseID);
    }

    public void addCoursesToShoppingCartByID(Set<Integer> courseIDs) {
        shoppingCart.addAll(courseIDs);
    }

    public void updateShoppingCart(Set<Integer> newCart) {
        shoppingCart.clear();
        addCoursesToShoppingCartByID(newCart);
    }

    public Course[] getUserCourses(CourseDAO courseDAO) {
        Course[] userCourse = new Course[courses.size()];
        int i = 0;

        for (int courseId : courses) {
            userCourse[i++] = courseDAO.getCourse(courseId);
        }
        return userCourse;

    }

    public Set<Integer> getCoursesBy() {
        return courses;
    }

    @Override
    public String toString() {
        return "User {" +
                "userName:" + userName +
                ", shoppingCart:" + shoppingCart +
                ", courses:" + courses +
                ", email: " + email +
                ", Name: " + name +
                ", address: " + address +
                "}";
    }
}
