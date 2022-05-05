import { Component, OnInit } from '@angular/core';

import { Course } from '../course';
import { CourseService } from '../course.service';
import { User } from '../User';
import { UserService } from '../user.service';
import { Router } from '@angular/router';

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

  userName?: String;

  constructor(
    private courseService: CourseService,
    private userService: UserService,
    private router: Router
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
    if (this.user != null && localStorage.getItem('user') != 'Admin') {
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
      }
      console.log(this.user);
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
