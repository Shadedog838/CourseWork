import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Course } from '../course';
import { User } from '../User';
import { UserService } from '../user.service';

@Component({
  selector: 'app-checkout-page',
  templateUrl: './checkout-page.component.html',
  styleUrls: ['./checkout-page.component.css'],
})
export class CheckoutPageComponent implements OnInit {
  courses!: Course[];
  price: number = 0;

  user: User = {
    userName: localStorage.getItem('user') || '',
    name: '',
    email: '',
    address: '',
    courses: [],
    shoppingCart: [],
    banned: false,
  };
  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    let courseId: number[] = [];
    let total = 0;
    this.userService
      .getUserShoppingCart(localStorage.getItem('user') || '')
      .subscribe((res) => {
        res.forEach((value) => {
          courseId.push(value.id);
          total += value.price;
        });
        this.courses = res;
        this.price = Math.round((total + Number.EPSILON) * 100) / 100;
        this.user.courses = courseId;
      });
  }

  addcourses() {
    this.userService.checkout(this.user);
    this.router.navigate(['']);
    alert('Thank You For Your Puchcase!');
  }
}
