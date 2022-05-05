import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from '../course';
import { User } from '../User';
import { UserService } from '../user.service';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./../../assets/css/bootstrap.css', './user-page.component.css'],
})
export class UserPageComponent implements OnInit {
  userName = localStorage.getItem('user') || '';
  registeredCourses: Course[] = [];
  userInfoForm!: FormGroup;
  user: User = {
    userName: '',
    name: '',
    email: '',
    address: '',
    shoppingCart: [],
    courses: [],
    banned: false,
  };
  broughtCourse!: Course[];
  editable = false;
  submitted = false;

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    // getting the user
    this.userService.getUser(this.userName).subscribe({
      next: (res) => {
        this.user = res;
      },
    });
    // getting user's registered courses
    this.userService
      .getUserCourses(this.userName)
      .subscribe((res) => (this.registeredCourses = res));
  }

  ngOnInit(): void {
    // setting up userInfo form
    this.userInfoForm = this.formBuilder.group({
      name: ['', [Validators.minLength(5), Validators.pattern('^[A-z]+ [A-z]+$')]],
          
      email: ['', [Validators.pattern("[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\\.[a-z]{2,3}")]],

      address: ['', [Validators.minLength(3)]],
    });
  }

  get f() {
    return this.userInfoForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    const formValue = this.userInfoForm.value;
    if (this.userInfoForm.invalid) {
      return;
    }
    if (formValue.name !== '') {
      this.user.name = formValue.name;
    }
    if (formValue.email !== '') {
      this.user.email = formValue.email;
    }
    if (formValue.address !== '') {
      this.user.address = formValue.address;
    }
    this.userService.updateUser(this.user).subscribe((res) => {
      this.user = res;
    });
    this.editable = false;
  }

  editMode() {
    this.editable = !this.editable;
    if (!this.editable) {
      location.reload();
    }
    return this.editable;
  }

  courseBtn(courseid: number) {
    this.router.navigate(['course-page/', courseid]);
  }
}
