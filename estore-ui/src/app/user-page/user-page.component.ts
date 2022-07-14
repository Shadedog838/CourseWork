import { renderFlagCheckIfStmt } from '@angular/compiler/src/render3/view/template';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Course } from '../course';
import { User } from '../User';
import { UserService } from '../user.service';
import { Image } from '../Image';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./../../assets/css/bootstrap.css', './user-page.component.css'],
})
export class UserPageComponent implements OnInit {
  userId = localStorage.getItem('user') || '';
  registeredCourses: Course[] = [];
  userInfoForm!: FormGroup;
  invalid = false;
  showPassword: boolean = false;
  selectedFile: any;
  imageUrl: any;
  user: User = {
    id: '',
    userName: '',
    password: '',
    email: '',
    image: {
      link: ''
    },
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
    this.userService.getUser(this.userId).subscribe({
      next: (res) => {
        this.user = res;
      },
    });
    // getting user's registered courses
    this.userService
      .getUserCourses(this.userId)
      .subscribe((res) => (this.registeredCourses = res));
  }

  ngOnInit(): void {
    // setting up userInfo form
    this.userInfoForm = this.formBuilder.group({
      image: [''],
      userName: ['', [
        Validators.minLength(4),
        Validators.maxLength(12)
      ]],

      password: ['', [Validators.minLength(6), Validators.maxLength(20)]],

      email: ['', [Validators.pattern("[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\\.[a-z]{2,3}"), Validators.maxLength(50)]],
    });
  }

  public onFileChanged(event: any) {
    let reader = new FileReader();
    this.selectedFile = event.target.files[0];
    if (event.target.files && event.target.files[0]) {
      reader.readAsDataURL(this.selectedFile);
      reader.onload = () => {
        this.imageUrl = reader.result;
      }
    }
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
    if (formValue.userName !== '') {
      this.user.userName = formValue.userName;
    }
    if (formValue.password !== '') {
      this.user.password = formValue.password;
    }
    if (formValue.email !== '') {
      this.user.email = formValue.email;
    }

    const image: Image = {
      link: this.imageUrl,
    };

    if (image.link != null) {
      this.user.image = image.link;
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

  showHidePassword() {
    this.showPassword = !this.showPassword;
  }

  courseBtn(courseid: string) {
    this.router.navigate(['course-page/', courseid]);
  }
}
