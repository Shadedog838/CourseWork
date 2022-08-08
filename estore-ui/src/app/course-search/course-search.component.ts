import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { Course } from '../course';
import { CourseService } from '../course.service';
import { User } from '../User';
import { UserService } from '../user.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-course-search',
  templateUrl: './course-search.component.html',
  styleUrls: ['./course-search.component.css'],
})
export class CourseSearchComponent implements OnInit {
  courses$!: Observable<Course[]>;
  user: User | undefined;
  userId?: string;

  options = {
    autoClose: true,
    keepAfterRouteChange: false,
  }


  constructor(
    private courseService: CourseService,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private toastr: ToastrService
  ) {
    this.userId = localStorage.getItem('user') || undefined;
  }

  ngOnInit(): void {
    this.searchCourses();
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.getUser();
  }

  searchCourses(): void {
    const title = String(this.route.snapshot.paramMap.get('title'));
    this.courses$ = this.courseService.searchCourses(title);
  }

  addCourseToCart(course: Course) {
    if (this.user != null) {
      if (
        !this.user.shoppingCart.includes(course.id) &&
        !this.user.courses.includes(course.id)
      ) {
        this.user.shoppingCart.push(course.id);
        this.toastr.success(course.title + ' has been added to your cart!', 'Success');
      } else {
        this.toastr.error(
          course.title + ' has already been added to your cart or purchased!',
          'Error'
        );
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
        .getUser(this.userId || '')
        .subscribe((userObj) => (this.user = userObj));
    }
  }
}
