import {Component, Inject, OnInit} from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {lesson} from "../lesson";

@Component({
  selector: 'app-lesson-modal',
  templateUrl: './lesson-modal.component.html',
  styleUrls: ['./lesson-modal.component.css'],
})
export class LessonModalComponent implements OnInit {
  model: lesson;
  form!: FormGroup;
  submitted = false;

  constructor(private dialogRef: MatDialogRef<LessonModalComponent>, @Inject(MAT_DIALOG_DATA) public lesson: lesson,
                                  private formBuilder: FormBuilder) {
    this.model = { ...lesson }; // copy lesson to model
  }

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      lessonTitle: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern('^[a-zA-Z0-9-.?:()\ \']+$')]],
      lessonVideo: ['', [Validators.required, Validators.pattern('^(https|http):\/\/(?:www\.)?youtube.com\/embed\/[A-z0-9]+')]]
    })
  }

  get f() { return this.form.controls }

  onSubmit() {
    this.submitted = true;

    if (this.form.invalid) {
      return;
    }
    this.close(this.model);
  }

  public close(lesson?: lesson) {
    this.dialogRef.close(lesson);
  }
}
