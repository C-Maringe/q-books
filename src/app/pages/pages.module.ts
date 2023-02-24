import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbToastModule, NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';

import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';

import { NzPaginationModule } from 'ng-zorro-antd/pagination';

import { FlatpickrModule } from 'angularx-flatpickr';
import { CountToModule } from 'angular-count-to';
import { NgApexchartsModule } from 'ng-apexcharts';
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
    ModalsComponent
  ],
  imports: [
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
    NzNotificationModule
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
