import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, tap } from 'rxjs';
import { User } from './User';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  // variable that keeps track of users logged in status;
  private userSubject : BehaviorSubject<string | undefined>
  // url to request a sign in
  private signInUrl = 'http://localhost:8080/users/login'
  //url to request a log in
  private registerUrl= "http://localhost:8080/users/register"

  public user: Observable<string | undefined>;

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  }


  constructor(
    private http:HttpClient
    ){
      this.userSubject = new BehaviorSubject<string | undefined>(localStorage.getItem("user") || undefined)
      this.user = this.userSubject.asObservable();
    }

    /*
      Gives the user name stored in the local storage
      @returns user name || undefined if there is no user is stored in the the local storage
    */
    public getuserName(): string | undefined {
      return this.userSubject.value;
    }


  // send post request to get user
   login(user : User): Observable<User> {
     return this.http.post<User>(this.signInUrl, user, this.httpOptions).pipe(
      tap((newUser: User) => {
        localStorage.setItem('user', newUser.userName)
        this.userSubject.next(newUser.userName);
        this.log(`signed in userName=${newUser.userName}`);
      }),
     );

  }

  // send a post request to create a new user
  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.registerUrl, user, this.httpOptions).pipe(
      tap((newUser: User) =>  {
        this.log(`added user w/ userName=${newUser.userName}`);
      }),
    );
  }

  logout() {
    // remove user from local storage and set userSubject to undefined
    localStorage.removeItem('user');
    this.userSubject.next(undefined);
  }

  private log(message:string){
    console.log(message);
  }
}
