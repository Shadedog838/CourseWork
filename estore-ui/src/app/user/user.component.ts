import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../User';
import { UserService } from '../user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit {
  userId?: string;
  user:User
  constructor(private userService: UserService, private router: Router) {
    this.userId = localStorage.getItem('user') || undefined;
    if (this.userId != undefined) {
      this.userService.getUser(this.userId).subscribe((user) => (this.user = user));
    }
  }

  ngOnInit(): void {

  }

  async logout() {
    this.userService.logout();
    this.router.navigate(['']);
    await new Promise<void>((done) => setTimeout(() => done(), 1));
    window.location.reload();
  }
}
