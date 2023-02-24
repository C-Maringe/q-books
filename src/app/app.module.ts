import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NZ_I18N, en_US } from 'ng-zorro-antd/i18n';

import { registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { StoreModule } from '@ngrx/store';
import { AuthGuard } from './auth.guard';
import { metaReducer, reducers } from './reducers';
import { NzNotificationModule } from 'ng-zorro-antd/notification';
registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    NzNotificationModule,
    StoreModule.forRoot(reducers, metaReducer)
  ],
  bootstrap: [AppComponent],
  providers: [
    AuthGuard,
    { provide: NZ_I18N, useValue: en_US }
  ]
})
export class AppModule { }
