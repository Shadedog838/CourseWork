import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit {
  user?: String;
  constructor(private userService: UserService, private router: Router) {
    this.user = localStorage.getItem('user') || undefined;
  }

  ngOnInit(): void {}

  async logout() {
    this.userService.logout();
    this.router.navigate(['']);
    await new Promise<void>((done) => setTimeout(() => done(), 1));
    window.location.reload();
  }
}
