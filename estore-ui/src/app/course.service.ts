import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { catchError, map, Observable, of, tap } from 'rxjs';

import { Course } from './course';

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  private coursesUrl = 'http://localhost:8080/courses'; // URL to web api

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  constructor(private http: HttpClient) {}

  /** GET courses from the server */
  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.coursesUrl).pipe(
      tap((_) => CourseService.log('fetched courses')),
      catchError(this.handleError<Course[]>('getCourses', []))
    );
  }

  /** GET course by id. Return  `undefined` when id not found */
  getCourseNo404<Data>(id: number): Observable<Course> {
    const url = `${this.coursesUrl}/?id=${id}`;
    return this.http.get<Course[]>(url).pipe(
      map((courses) => courses[0]), // returns a {0|1} element array
      tap((h) => {
        const outcome = h ? 'fetched' : 'did not find';
        CourseService.log(`${outcome} course id=${id}`);
      }),
      catchError(this.handleError<Course>(`getCourse id=${id}`))
    );
  }

  /** Get course by id. Will 404 if id not found */
  getCourse(id: number): Observable<Course> {
    const url = `${this.coursesUrl}/${id}`;
    return this.http.get<Course>(url).pipe(
      tap((_) => CourseService.log(`fetched course id=${id}`)),
      catchError(this.handleError<Course>(`getCourse id=${id}`))
    );
  }

  /* GET courses whose name contains search term */
  searchCourses(term: string): Observable<Course[]> {
    if (!term.trim()) {
      // if not search term, return empty course array.
      return of([]);
    }
    return this.http.get<Course[]>(`${this.coursesUrl}/?title=${term}`).pipe(
      tap((x) =>
        x.length
          ? CourseService.log(`found courses matching "${term}"`)
          : CourseService.log(`no courses matching "${term}"`)
      ),
      catchError(this.handleError<Course[]>('searchCourses', []))
    );
  }

  /**
   * Adds a new course to the server
   *
   * @param course new course to add
   * @param userName user name of the user adding the course
   */
  addCourse(course: Course, userName: string): Observable<Course> {
    return this.http
      .post<Course>(
        this.coursesUrl,
        { data: course, userName },
        this.httpOptions
      )
      .pipe(
        tap((newCourse: Course) => {
          CourseService.log(`added following course`);
          CourseService.log(JSON.stringify(newCourse));
        }),
        catchError(this.handleError<Course>('addCourse'))
      );
  }

  /**
   * Updates a course on the server
   *
   * @param course course to update
   * @param userName user name of the user updating the course
   */
  updateCourse(course: Course, userName: string): Observable<Course> {
    return this.http
      .put<Course>(
        this.coursesUrl,
        { data: course, userName },
        this.httpOptions
      )
      .pipe(
        tap((_) => CourseService.log(`updated course id=${course.id}`)),
        catchError(this.handleError<any>('updateCourse'))
      );
  }

  /**
   * Deletes a course from the server
   *
   * @param id ID of the course to delete
   */
  deleteCourse(id: number) {
    const url = `${this.coursesUrl}/${id}`;
    return this.http.delete<Course>(url, this.httpOptions).pipe(
      tap((_) => CourseService.log(`deleted course id=${id}`)),
      catchError(this.handleError<Course>('deleteCourse'))
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

      // TODO: better job of transforming error for user consumption
      CourseService.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a CourseService message with the MessageService */
  private static log(message: string) {
    console.log(message);
  }
}
