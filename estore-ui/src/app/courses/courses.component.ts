import { Component, OnInit } from '@angular/core';

import { Course } from '../course';
import { CourseService } from '../course.service';
import { User } from '../User';
import { UserService } from '../user.service';
import { Router } from '@angular/router';
import { AlertService } from '../alert';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css'],
})
export class CoursesComponent implements OnInit {
  courses: Course[] = [];
  recommendedCourses: Course[] = [];

  user: User | undefined;
  userPrevious: User | undefined;

  options = {
    autoClose: true,
    keepAfterRouteChange: false,
  }

  constructor(
    private courseService: CourseService,
    private userService: UserService,
    private router: Router,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.userPrevious = undefined;
    this.getCourses();
    this.getUser();
    this.getRecommendedCourses();
  }

  ngDoCheck(): void {
    if (this.userPrevious != this.user) {
      this.userPrevious = this.user;
      this.getRecommendedCourses();
    }
  }

  getCourses(): void {
    this.courseService
      .getCourses()
      .subscribe((courses) => (this.courses = courses));
  }

  getRecommendedCourses(): void {
    if (this.user != null && this.user.userName != 'Admin') {
      this.userService
        .getRecommendedCourses(this.user)
        .subscribe((rCourses) => (this.recommendedCourses = rCourses));
    }
  }

  addCourseToCart(course: Course) {
    if (this.user != null) {
      if (
        !this.user.shoppingCart.includes(course.id) &&
        !this.user.courses.includes(course.id)
      ) {
        this.user.shoppingCart.push(course.id);
        this.alertService.success("New course added to cart!", this.options);
      } else {
        this.alertService.error("This course has already been added to cart or purchased!", this.options);
      }
      this.userService
        .updateUser(this.user)
        .subscribe((userObj) => (this.user = userObj));
    } else {
      this.router.navigate(['/account/login']);
    }
  }

  getUser(): void {
    if (!this.userService.getloginStatus()) {
      this.user = undefined;
    } else {
      this.userService
        .getUser(localStorage.getItem('user') || '')
        .subscribe((userObj) => (this.user = userObj));
    }
  }
}
