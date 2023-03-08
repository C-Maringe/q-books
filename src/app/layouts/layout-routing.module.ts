import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EmployeeGuard } from './employee.guard';
import { ClientGuard } from './client.guard';
import { LayoutComponent } from './main-layout/layout.component';

// Component Pages

const routes: Routes = [
  { path: '', component: LayoutComponent, loadChildren: () => import('../pages/pages.module').then(m => m.PagesModule), canActivate: [EmployeeGuard] },
  { path: '', component: LayoutComponent, loadChildren: () => import('../client-pages/client-pages.module').then(m => m.ClientPagesModule), canActivate: [ClientGuard] },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LayoutRoutingModule { }
