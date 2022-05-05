import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CourseSearchComponent } from './course-search/course-search.component';
import { CoursesComponent } from './courses/courses.component';
import { AuthGuard } from './helpers/auth.guard';
import { UserComponent } from './user/user.component';
import { CourseFormComponent } from './course-form/course-form.component';
import { ShoppingCartPageComponent } from './shopping-cart-page/shopping-cart-page.component';
import { CoursePageComponent } from './course-page/course-page.component';
import { CheckoutPageComponent } from './checkout-page/checkout-page.component';

const accountModule = () =>
  import('./account/account.module').then((x) => x.AccountModule);

const routes: Routes = [
  { path: '', component: CoursesComponent },
  { path: 'search/:title', component: CourseSearchComponent },
  { path: 'course-form', component: CourseFormComponent }, // Add canActivate: [AuthGuard] to things that required the user be signed in
  { path: 'user', component: UserComponent, canActivate: [AuthGuard] },
  { path: 'account', loadChildren: accountModule },
  {
    path: 'cart',
    component: ShoppingCartPageComponent,
    canActivate: [AuthGuard],
  },
  { path: 'course-page/:id', component: CoursePageComponent },
  {
    path: 'checkout',
    component: CheckoutPageComponent,
    canActivate: [AuthGuard],
  },
  // otherwise redirect to home
  { path: '*', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
