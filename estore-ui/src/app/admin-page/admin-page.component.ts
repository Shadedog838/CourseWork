import { Component, OnInit } from '@angular/core';
import { Course } from '../course';
import { filter } from 'rxjs/operators';
import { CourseService } from '../course.service';
import { User } from '../User';
import { UserService } from '../user.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ConfirmModalComponent } from '../confirm-modal/confirm-modal.component';

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.css'],
})
export class AdminPageComponent implements OnInit {
  courses: Course[] = [];
  users: User[] = [];
  adminId: string;

  constructor(
    private courseService: CourseService,
    private userService: UserService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.adminId = localStorage.getItem('user') ?? '';
    this.getCourses();
    this.getUsers();
  }

  getCourses() {
    this.courseService
      .getCourses()
      .subscribe((courses) => (this.courses = courses));
  }

  deleteCourse(id: string) {
    this.courseService.deleteCourse(id).subscribe(() => this.getCourses());
  }

  banUser(userId: string) {
    this.userService
      .banUser(userId, this.adminId)
      .subscribe(() => this.getUsers());
  }

  getUsers() {
    this.userService
      .getUsers(this.adminId)
      .subscribe(
        (users) =>
          (this.users = users.filter(
            (user) => user.userName.toLowerCase() !== 'admin'
          ))
      );
  }

  unbanUser(userId: string) {
    this.userService
      .unbanUser(userId, this.adminId)
      .subscribe(() => this.getUsers());
  }

  openConfirmModal(title: string, message: string, course: Course) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = { message, title };
    dialogConfig.width = 500 + 'px';
    dialogConfig.height = 150 + 'px';
    this.dialog
      .open(ConfirmModalComponent, dialogConfig)
      .afterClosed()
      .subscribe((result: boolean) => {
        if (result) {
          this.courseService.deleteCourse(course.id).subscribe(() => {
            this.getCourses();
            // console.log('Course deleted');
          });
        }
      });
  }
}
