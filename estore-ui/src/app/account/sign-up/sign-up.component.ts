import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { flatMap } from 'rxjs';
import { UserService } from 'src/app/user.service';

import { User } from '../../User';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./../../../assets/css/bootstrap.css', './sign-up.component.css'],
})
export class SignUpComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  submitted = false;
  invalid = false;
  showPassword: boolean = false;
  generatedPassword:boolean = false;
  password:string = '';

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {
    if (this.userService.getloginStatus()) {
      this.router.navigate(['']);
    }
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      userName: [
        '',
        [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(12),
        ],
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.maxLength(20),
        ],
      ],
      email: [
        '',
        [
          Validators.required,
          Validators.pattern('[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\\.[a-z]{2,3}'),
          Validators.maxLength(50),
        ],
      ],
    });
  }

  showHidePassword() {
    this.showPassword = !this.showPassword;
  }

  genPassword() {
    this.password = '';
    const chars = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const passwordLength= 19;
    for (let i=0; i<= passwordLength; ++i) {
      let randomNumber = Math.floor(Math.random() * chars.length);
      this.password += chars.substring(randomNumber, randomNumber+1);
    }
    this.generatedPassword = true;
  }

  get f() {
    return this.form.controls;
  }

  onSubmit() {
    this.submitted = true;

    if (this.form.invalid) {
      return;
    }

    this.loading = true;
    let user: User;
    user = this.form.value;
    user.shoppingCart = [];
    user.courses = [];
    this.userService.createUser(user).subscribe({
      next: () => {
        this.router.navigate(['../login'], { relativeTo: this.route });
      },
      error: () => {
        this.loading = false;
        this.invalid = true;
      },
    });
  }
}
