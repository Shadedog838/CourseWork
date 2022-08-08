import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Course } from '../course';
import { CourseService } from '../course.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ModalComponent } from '../modal/modal.component';
import { lesson } from '../lesson';
import { UserService } from '../user.service';
import { User } from '../User';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-course-page',
  templateUrl: './course-page.component.html',
  styleUrls: ['./course-page.component.css'],
})
export class CoursePageComponent implements OnInit {
  course: Course | undefined;
  userName: string | undefined;
  user: User | undefined;

  options = {
    autoClose: true,
    keepAfterRouteChange: false,
  }

  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private matDialog: MatDialog,
    private userService: UserService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.getCourse();
    this.getUser();
  }

  hasCourses(course: Course): boolean {
    return !!this.user?.courses.includes(course.id);
  }

  getCourse(): void {
    const id = String(this.route.snapshot.paramMap.get('id'));
    this.courseService
      .getCourse(id)
      .subscribe((course) => (this.course = course));
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
        .getUser(localStorage.getItem('user') || '')
        .subscribe((userObj) => (this.user = userObj));
    }
  }

  openModal(lesson: lesson, course: Course): void {
    if (this.user != undefined) {
      if (
        this.user?.courses.includes(course.id) ||
        this.user.userName.toLowerCase() == 'admin'
      ) {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.disableClose = false;
        dialogConfig.autoFocus = true;
        const relativeWidth = 800;
        const relativeHeight = 640;
        dialogConfig.width = relativeWidth + 'px';
        dialogConfig.height = relativeHeight + 'px';
        dialogConfig.data = {
          title: lesson.title,
          video: lesson.video,
        };
        this.matDialog.open(ModalComponent, dialogConfig);
      } else {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.disableClose = false;
        dialogConfig.autoFocus = true;
        const relativeWidth = 800;
        const relativeHeight = 640;
        dialogConfig.width = relativeWidth + 'px';
        dialogConfig.height = relativeHeight + 'px';
        dialogConfig.data = {
          title: lesson.title,
          video: 'restricted',
        };
        this.matDialog.open(ModalComponent, dialogConfig);
      }
    } else {
      this.router.navigate(['/account/login']);
    }
  }
}
