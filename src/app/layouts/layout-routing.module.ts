import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LayoutComponent } from './main-layout/layout.component';

// Component Pages

const routes: Routes = [
  { path: '', component: LayoutComponent, loadChildren: () => import('../pages/pages.module').then(m => m.PagesModule) },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LayoutRoutingModule { }
