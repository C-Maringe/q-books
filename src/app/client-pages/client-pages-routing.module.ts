import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClientProductsComponent } from './client-products/client-products.component';
import { ClientProfileComponent } from './client-profile/client-profile.component';
import { ClientScheduleComponent } from './client-schedule/client-schedule.component';

// Component pages

const routes: Routes = [
  { path: "client/products", component: ClientProductsComponent },
  { path: "client/profile", component: ClientProfileComponent },
  { path: "client/schedule", component: ClientScheduleComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientPagesRoutingModule { }
