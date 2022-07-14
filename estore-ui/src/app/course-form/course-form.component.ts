import { Component, OnInit } from '@angular/core';
import { CourseService } from '../course.service';
import { Course } from '../course';
import { ActivatedRoute, Router } from '@angular/router';
import { lesson } from '../lesson';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { LessonModalComponent } from '../lesson-modal/lesson-modal.component';
import { Image } from '../Image';
import { FormGroup } from '@angular/forms';

type CourseFormModel = {
  title: string;
  description: string;
  content: lesson[];
  price: number;
  tags: string;
};

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.css'],
})
export class CourseFormComponent implements OnInit {
  model: CourseFormModel = {
    title: '',
    description: '',
    content: [],
    price: 0,
    tags: '',
  };

  form!: FormGroup;
  course: Course;
  title = 'Course Form';
  selectedFile: any;
  imageUrl: any;

  constructor(
    private courseService: CourseService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
  ) {}

  public onFileChanged(event: any) {
    //Select File
    let reader = new FileReader();
    this.selectedFile = event.target.files[0];
    if (event.target.files && event.target.files[0]) {
      reader.readAsDataURL(this.selectedFile);
      reader.onload = () => {
        this.imageUrl = reader.result;
      };
    }
  }

  submit = (course: Course, userId: string) => {
    this.courseService
      .addCourse(course, userId)
      .subscribe((course: Course) => {
        this.router.navigate(['/user']);
      });
  };

  openLessonModal(lesson?: lesson, edit = false) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.data = lesson ?? { title: '', video: '' };
    dialogConfig.width = 500 + 'px';
    dialogConfig.height = 250 + 'px';
    this.dialog
      .open(LessonModalComponent, dialogConfig)
      .afterClosed()
      .subscribe((newLesson: lesson) => {
        if (newLesson) {
          if (edit) {
            this.model.content[this.model.content.indexOf(lesson!)] = newLesson;
          } else {
            this.model.content.push(newLesson);
          }
        }
      });
  }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((params) => {
      const courseId = params.get('courseId');
      const title = params.get('formTitle');
      this.title = title || 'Course Form';
      if (courseId == null) {
        return;
      }
      this.courseService.getCourse(courseId).subscribe((course) => {
        this.imageUrl = course.image.link;
        this.model = {
          title: course.title,
          description: course.description,
          content: course.content,
          price: course.price,
          tags: course.tags.join(','),
        };
        // this.form = this.formBuilder.group({
        //   title: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern('^[a-zA-Z0-9-.?:()\ \']+$')]]
        // })
        this.course = course;
        this.submit = (course: Course, userId: string) => {
          this.courseService
            .updateCourse(course, userId)
            .subscribe((course) => {
              this.router.navigate(['/user']);
            });
        };
      });
    });
  }

  get f() { return this.form.controls }

  onSubmit() {
    const image: Image = {
      link: this.imageUrl,
    };
    const course: Course = {
      image: image,
      title: this.model.title,
      description: this.model.description,
      price: this.model.price,
      tags: this.model.tags
        .split(',')
        .map((tag) => tag.trim().toLowerCase())
        .filter((tag) => tag.length > 0),
      id: this.course?.id ?? -1,
      content: this.model.content,
      studentsEnrolled: this.course?.studentsEnrolled ?? 0,
    };
    this.submit(course, localStorage.getItem('user') ?? '');
  }

  removeLesson(lesson: lesson) {
    this.model.content = this.model.content.filter((l) => l !== lesson);
  }
}
