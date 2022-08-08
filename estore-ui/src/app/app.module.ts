import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TopBarComponent } from './top-bar/top-bar.component';
import { CoursesComponent } from './courses/courses.component';
import { CourseSearchComponent } from './course-search/course-search.component';
import { CourseFormComponent } from './course-form/course-form.component';
import { UserComponent } from './user/user.component';
import { AdminPageComponent } from './admin-page/admin-page.component';
import { ShoppingCartPageComponent } from './shopping-cart-page/shopping-cart-page.component';
import { UserPageComponent } from './user-page/user-page.component';
import { TabDisplayComponent } from './tab-display/tab-display.component';
import { TabComponent } from './tab/tab.component';
import { CoursePageComponent } from './course-page/course-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule } from '@angular/material/dialog';
import { ModalComponent } from './modal/modal.component';
import { LessonModalComponent } from './lesson-modal/lesson-modal.component';
import { ConfirmModalComponent } from './confirm-modal/confirm-modal.component';
import { CheckoutPageComponent } from './checkout-page/checkout-page.component';
import { ToastrModule } from 'ngx-toastr';

@NgModule({
  declarations: [
    AppComponent,
    TopBarComponent,
    CoursesComponent,
    CourseSearchComponent,
    CourseFormComponent,
    UserComponent,
    AdminPageComponent,
    ShoppingCartPageComponent,
    UserPageComponent,
    CoursePageComponent,
    ModalComponent,
    UserPageComponent,
    CheckoutPageComponent,
    TabDisplayComponent,
    TabComponent,
    LessonModalComponent,
    ConfirmModalComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatDialogModule,
    ToastrModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
