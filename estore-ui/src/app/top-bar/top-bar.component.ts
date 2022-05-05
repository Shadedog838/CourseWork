import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { debounceTime } from 'rxjs';
import { UserService } from '../user.service';

import { User } from '../User';
@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css'],
})
export class TopBarComponent implements OnInit {
  userName: string | undefined = undefined;
  userObj: User | undefined;
  constructor(private router: Router, private userService: UserService) {
    if (userService.getloginStatus()) {
      this.userName = localStorage.getItem('user') || '';
    }
  }

  ngOnInit(): void {
    this.getUser();
  }

  search(term: string): void {
    debounceTime(500);
    if (term == '') {
      this.router.navigate(['']);
    } else {
      this.router.navigate(['/search', term]);
    }
  }

  getUser(): void {
    if (this.userName === undefined) return;
    this.userService
      .getUser(this.userName)
      .subscribe((userObj) => (this.userObj = userObj));
  }
}
