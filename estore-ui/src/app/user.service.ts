import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import {
  BehaviorSubject,
  catchError,
  Observable,
  of,
  Subject,
  tap,
} from 'rxjs';

import { Course } from './course';
import { User } from './User';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private usersUrl = 'http://localhost:8080/users'; // URL to web api
  // stores the user as an observable
  private readonly user: Subject<User>;
  // stores the login status of the user as an observable
  private loginStatus: BehaviorSubject<boolean>;
  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  constructor(private http: HttpClient) {
    this.user = new Subject<User>();
    this.loginStatus = new BehaviorSubject<boolean>(false);
    let userId = localStorage.getItem('user');
    if (
      userId != null
      // userName.match('^[a-zA-z]+[0-9]+$ || Admin') &&
      // userName.length >= 4 &&
      // userName.length <= 10
    ) {
      this.loginStatus.next(true);
    } else {
      this.loginStatus.next(false);
    }
  }

  // Returns if the user is logged in or not
  getloginStatus(): boolean {
    return this.loginStatus.value;
  }

  // get the user Observable
  getUserObservable(): Observable<User> {
    return this.user;
  }
  /** Get course by id. Will 404 if userName not found */
  getUser(userId: string): Observable<User> {
    const url = `${this.usersUrl}/${userId}`;
    this.http
      .get<User>(url)
      .pipe(
        tap((_) => UserService.log(`fetched user userId=${userId}`)),
        catchError(this.handleError<User>(`getUser userId=${userId}`))
      )
      .subscribe((user) => this.user.next(user));
    return this.user;
  }

  /** GET recommended courses from the server for a specific user */
  getRecommendedCourses(usr: User | undefined): Observable<Course[]> {
    let url;
    if (usr == null) {
      url = `${this.usersUrl}/null/recommended/0`; // TODO: get rid of hardcoded value
    } else {
      url = `${this.usersUrl}/${usr.id}/recommended/5`; // TODO: get rid of hardcoded value
    }
    return this.http.get<Course[]>(url).pipe(
      tap((_) => UserService.log('fetched recommendedCourses')),
      catchError(this.handleError<Course[]>('getRecommendedCourses', []))
    );
  }

  /* GET shopping cart associated with user */
  getUserShoppingCart(userId: string): Observable<Course[]> {
    const url = `${this.usersUrl}/${userId}/cart`;
    return this.http.get<Course[]>(url).pipe(
      tap((_) => UserService.log(`fetched user cart userId=${userId}`)),
      catchError(
        this.handleError<Course[]>(`getUserShoppingCart userId=${userId}`)
      )
    );
  }

  getUserCourses(userId: string): Observable<Course[]> {
    const url = `${this.usersUrl}/${userId}/courses`;
    return this.http.get<Course[]>(url).pipe(
      tap((_) =>
        UserService.log(`fetched user courses userId = ${userId}`)
      ),
      catchError(
        this.handleError<Course[]>(`getUserCourses userId=${userId}`)
      )
    );
  }
  /**
   * Updates the associated user
   * @param user
   * @returns User Observable
   */
  updateUser(user: User): Observable<User> {
    this.http.put<User>(`${this.usersUrl}/${user.id}`, user, this.httpOptions)
      .pipe(
        tap((updatedUser: User) => {
          UserService.log(`updated following user`);
          UserService.log(JSON.stringify(updatedUser));
        }),
        catchError(this.handleError<User>('updateUser'))
      )
      .subscribe((user) => this.user.next(user));
    return this.user;
  }

  checkout(newCourse: User) {
    const checkoutUrl = `${this.usersUrl}/checkout`;
    this.http
      .put<User>(checkoutUrl, newCourse, this.httpOptions)
      .pipe(
        tap((_) => {
          UserService.log(`updated following user`);
        }),
        catchError(this.handleError<User>('updateUser'))
      )
      .subscribe((user) => this.user.next(user));
  }

  /** GET all users from the server */
  getUsers(userId: string): Observable<User[]> {
    const url = `${this.usersUrl}?id=${userId}`;
    return this.http.get<User[]>(url).pipe(
      tap((_) => UserService.log('fetched users')),
      catchError(this.handleError<User[]>('getUsers', []))
    );
  }

  /** POST: ban a user */
  banUser(userId: string, requesterName: string): Observable<User> {
    const url = `${this.usersUrl}/${userId}/ban`;
    this.http
      .post<User>(url, requesterName, this.httpOptions)
      .pipe(
        tap((updatedUser: User) => {
          UserService.log(`updated following user`);
          UserService.log(JSON.stringify(updatedUser));
        }),
        catchError(this.handleError<User>('banUser'))
      )
      .subscribe((user) => {
        const userId = localStorage.getItem('user');
        user.id = userId ?? user.id;
        this.user.next(user);
      });
    return this.user;
  }

  /** POST: unban a user */
  unbanUser(userId: string, requesterName: string): Observable<User> {
    const url = `${this.usersUrl}/${userId}/unban`;
    this.http
      .post<User>(url, requesterName, this.httpOptions)
      .pipe(
        tap((updatedUser: User) => {
          UserService.log(`updated following user`);
          UserService.log(JSON.stringify(updatedUser));
        }),
        catchError(this.handleError<User>('unbanUser'))
      )
      .subscribe((user) => {
        const userId = localStorage.getItem('user');
        user.id = userId ?? user.id;
        this.user.next(user);
      });
    return this.user;
  }
  /**
   * Log's out the user
   */
  logout() {
    localStorage.removeItem('user');
    this.loginStatus.next(false);
  }

  /**
   * Log's in the user
   * @param user
   * @returns user Observable
   */
  login(user: User): Observable<User> {
    const url = `${this.usersUrl}/login`;
    return this.http.post<User>(url, user, this.httpOptions).pipe(
      tap((res) => {
        UserService.log('user Logged in');
        localStorage.setItem('user', res.id);
        this.loginStatus.next(true);
      })
    );
  }

  /**
   * Creates a new User
   * @param user
   * @returns user Observable
   */
  createUser(user: User): Observable<User> {
    const url = `${this.usersUrl}/register`;
    return this.http.post<User>(url, user, this.httpOptions).pipe(
      tap((newUser: User) => {
        UserService.log(`added user w/ userId=${newUser.id}`);
      })
    );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   *
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
      if (
        (error.status === 403 || error.status == 404) &&
        this.getloginStatus()
      ) {
        this.logout();
        //location.replace('');
      }

      // TODO: better job of transforming error for user consumption
      UserService.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a UserService message with the MessageService */
  private static log(message: string) {
    console.log(message);
  }
}
