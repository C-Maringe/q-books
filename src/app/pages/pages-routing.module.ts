import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Component pages
import { AnalyticsComponent } from './analytics/analytics.component';
import { BookingsComponent } from './bookings/bookings.component';
import { CashUpComponent } from './cash-up/cash-up.component';
import { ClientsComponent } from './clients/clients.component';
import { HumanResourceComponent } from './human-resource/human-resource.component';
import { MarketingComponent } from './marketing/marketing.component';
import { ModalsComponent } from './modals/modals.component';
import { ProductsComponent } from './products/products.component';
import { ReportingComponent } from './reporting/reporting.component';
import { ScheduleComponent } from './schedule/schedule.component';
import { ServicesComponent } from './services/services.component';
import { SettingsComponent } from './settings/settings.component';

const routes: Routes = [
  { path: "schedule", component: ScheduleComponent },
  { path: "analytics", component: AnalyticsComponent },

  { path: "bookings", component: BookingsComponent },
  { path: "cash-up", component: CashUpComponent },

  { path: "clients", component: ClientsComponent },
  { path: "hr", component: HumanResourceComponent },

  { path: "marketing", component: MarketingComponent },
  { path: "products", component: ProductsComponent },

  { path: "reporting", component: ReportingComponent },
  { path: "services", component: ServicesComponent },

  { path: "settings", component: SettingsComponent },

  // To be removed
  { path: "modals", component: ModalsComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PagesRoutingModule { }
