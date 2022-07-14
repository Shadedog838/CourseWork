import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Course } from '../course';
import { User } from '../User';
import { UserService } from '../user.service';
import { concatMap } from 'rxjs';

@Component({
  selector: 'app-shopping-cart-page',
  templateUrl: './shopping-cart-page.component.html',
  styleUrls: ['./shopping-cart-page.component.css'],
})
export class ShoppingCartPageComponent implements OnInit {
  public cart: Course[] = [];
  user: User | undefined = undefined;

  constructor(private router: Router, private userService: UserService) {
    if ( !this.userService.getloginStatus()) {
      this.router.navigate(['']);
    }
  }

  ngOnInit(): void {
    this.getUser();
  }

  getCart(): void {

    if (this.userService.getloginStatus()) {
      this.userService
        .getUserShoppingCart(this.user?.userName || '')
        .subscribe((courses) => (this.cart = courses));
    }
  }

  getUser(): void {
    if (!this.userService.getloginStatus()) {
      this.user = undefined;
    } else {
      this.userService
        .getUser(localStorage.getItem('user') || '')
        .pipe(
          concatMap((value) => {
            this.user = value;
            return this.userService.getUserShoppingCart(value.id);
          })
        )
        .subscribe((userObj) => (this.cart = userObj));
    }
  }

  removeCourseFromCart(course: Course) {
    if (this.user != null) {
      if (this.user.shoppingCart.includes(course.id)) {
        this.user.shoppingCart = this.user.shoppingCart.filter(
          (c) => c !== course.id
        );
      }
      this.userService
        .updateUser(this.user)
        .subscribe((userObj) => (this.user = userObj));
      this.getCart();
    }
  }
  checkout() {
    this.router.navigate(['checkout']);
  }
}
