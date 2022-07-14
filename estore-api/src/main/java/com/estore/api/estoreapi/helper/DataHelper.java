package com.estore.api.estoreapi.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.estore.api.estoreapi.models.Course;
import com.estore.api.estoreapi.models.User;
import com.estore.api.estoreapi.ordering.OrderByName;
import com.estore.api.estoreapi.ordering.OrderByPopularity;
import com.estore.api.estoreapi.ordering.OrderByPrice;
import com.estore.api.estoreapi.repository.CourseRepository;
import com.estore.api.estoreapi.repository.UserRepository;

@Service
public class DataHelper {
  private static final Logger LOG = Logger.getLogger(DataHelper.class.getName());

  CourseRepository courseRepository;
  UserRepository userRepository;

  public DataHelper(CourseRepository courseRepository, UserRepository userRepository) {
    this.courseRepository = courseRepository;
    this.userRepository = userRepository;
  }

  public Course[] getRecommendedCoursesForUser(User user) {
    if (user == null) {
      return new Course[0];
    }
    List<Course> allcourseList = courseRepository.findAll();
    Course[] allCourses = allcourseList.toArray(new Course[0]);
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

  public Course getCourse(String id) {
    LOG.info("ID: " + id);
    Optional<Course> courseData = courseRepository.findById(id);
    return courseData.get();
  }

  public User updateUserCourses(String id, Set<String> courses) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      for (String courseid : courses) {
        Course course = courseRepository.findById(courseid).get();
        int enrolled = course.getStudentsEnrolled();
        enrolled += 1;
        course.setStudentsEnrolled(enrolled);
        courseRepository.save(course);
      }
      user.get().updateUserCourse(courses);
      return user.get();
    }
    return null;
  }

  public ArrayList<Course> getCoursesByPrice(Double price, List<Course> courses) {
    ArrayList<Course> courseArrayList = new ArrayList<>();

    for (Course course : courses) {
      if (course.getPrice() <= price) {
        courseArrayList.add(course);
      }
    }
    return courseArrayList;
  }

  public Course[] getCoursesArray(String containsText, List<Course> courses) { // if containsText == null, no filter
    ArrayList<Course> courseArrayList = new ArrayList<>();
    Boolean byPrice = false;

    for (Course course : courses) {
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
          courseArrayList = getCoursesByPrice(num, courses);
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

  public boolean deleteCourse(String id) {
    List<User> users = userRepository.findAll();
    if (courseRepository.findById(id).isPresent()) {
      for (User user : users) {
        Set<String> cart = user.getShoppingCart();
        Set<String> registerdCourses = user.getCourses();
        cart.remove(id);
        registerdCourses.remove(id);
        userRepository.save(user);
      }
      courseRepository.deleteById(id);
      return true;
    } else
      return false;
  }

}
