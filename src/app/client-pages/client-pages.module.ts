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
import { NzModalModule } from 'ng-zorro-antd/modal';


import { ClientProfileComponent } from './client-profile/client-profile.component';
import { ClientScheduleComponent } from './client-schedule/client-schedule.component';
import { ClientProductsComponent } from './client-products/client-products.component';
import { ClientPagesRoutingModule } from './client-pages-routing.module';

import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { NzNotificationModule } from 'ng-zorro-antd/notification';
import { NzDrawerModule } from 'ng-zorro-antd/drawer';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { CalendarModule, DateAdapter } from 'angular-calendar';


const DEFAULT_SWIPER_CONFIG: SwiperConfigInterface = {
  direction: 'horizontal',
  slidesPerView: 'auto'
};

@NgModule({
  declarations: [
    ClientProfileComponent,
    ClientScheduleComponent,
    ClientProductsComponent
  ],
  imports: [
    CommonModule,
    ClientPagesRoutingModule,
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
  ], providers: [
    {
      provide: SWIPER_CONFIG,
      useValue: DEFAULT_SWIPER_CONFIG
    }
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ClientPagesModule { }
