import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbToastModule, NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';

// Apex Charts
import { NgApexchartsModule } from 'ng-apexcharts';
import { NgChartsModule } from 'ng2-charts';
import { NgxEchartsModule } from 'ngx-echarts';

import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';

import { NzPaginationModule } from 'ng-zorro-antd/pagination';

import { FlatpickrModule } from 'angularx-flatpickr';
import { CountToModule } from 'angular-count-to';
import { NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';

import { NzTableModule } from 'ng-zorro-antd/table';

// Swiper Slider
import { SWIPER_CONFIG } from 'ngx-swiper-wrapper';
import { SwiperConfigInterface } from 'ngx-swiper-wrapper';

import { LightboxModule } from 'ngx-lightbox';

import { NzDropDownModule } from 'ng-zorro-antd/dropdown';

// Load Icons
import { defineLordIconElement } from 'lord-icon-element';
import lottie from 'lottie-web';
import { NzModalModule } from 'ng-zorro-antd/modal';

// Pages Routing
import { PagesRoutingModule } from "./pages-routing.module";
import { ScheduleComponent } from './schedule/schedule.component';
import { BookingsComponent } from './bookings/bookings.component';
import { CashUpComponent } from './cash-up/cash-up.component';
import { ServicesComponent } from './services/services.component';
import { ProductsComponent } from './products/products.component';
import { HumanResourceComponent } from './human-resource/human-resource.component';
import { ClientsComponent } from './clients/clients.component';
import { SettingsComponent } from './settings/settings.component';
import { MarketingComponent } from './marketing/marketing.component';
import { ReportingComponent } from './reporting/reporting.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { NzDrawerModule } from 'ng-zorro-antd/drawer';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { ModalsComponent } from './modals/modals.component';
import { NzNotificationModule } from 'ng-zorro-antd/notification';

import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { ProfileComponent } from './clientPages/profile/profile.component';



import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DropdownModule } from 'primeng/dropdown';
import { RadioButtonModule } from 'primeng/radiobutton';
import { RatingModule } from 'primeng/rating';
import { ToolbarModule } from 'primeng/toolbar';

const DEFAULT_SWIPER_CONFIG: SwiperConfigInterface = {
  direction: 'horizontal',
  slidesPerView: 'auto'
};

@NgModule({
  declarations: [
    ScheduleComponent,
    BookingsComponent,
    CashUpComponent,
    ServicesComponent,
    ProductsComponent,
    HumanResourceComponent,
    ClientsComponent,
    SettingsComponent,
    MarketingComponent,
    ReportingComponent,
    AnalyticsComponent,

    //to be removed
    ModalsComponent,
    ProfileComponent
  ],
  imports: [
    NgApexchartsModule,
    NgChartsModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    }),
    CommonModule,
    NgbToastModule,
    NgbProgressbarModule,
    FlatpickrModule.forRoot(),
    CalendarModule.forRoot({
      provide: DateAdapter,
      useFactory: adapterFactory,
    }),
    NgbModalModule,
    CountToModule,
    NgApexchartsModule,
    NgbDropdownModule,
    NgbNavModule,
    PagesRoutingModule,
    LightboxModule,
    FormsModule,
    NzDropDownModule,
    NzPaginationModule,
    NzTableModule,
    NzDatePickerModule,
    NgClass,
    NzModalModule,
    NzDrawerModule,
    NzNotificationModule,
    InputTextModule,
    DialogModule,
    ToolbarModule,
    ConfirmDialogModule,
    RatingModule,
    InputNumberModule,
    InputTextareaModule,
    RadioButtonModule,
    DropdownModule,
    ButtonModule,
    TableModule
  ],
  providers: [
    {
      provide: SWIPER_CONFIG,
      useValue: DEFAULT_SWIPER_CONFIG
    }
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PagesModule {
  constructor() {
    defineLordIconElement(lottie.loadAnimation);
  }
}
