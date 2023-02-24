import { Component, ElementRef, Renderer2 } from '@angular/core';
import { Store } from '@ngrx/store';
import * as moment from 'moment';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';
import { tokenData } from 'src/app/reducers/token/token.reducer';

import { GridJs } from './data';

interface ScheduleItems {
  employeeId: string,
  employeeFullName: string,
  employeeTitle: string
}

interface SearchForm {
  search: string
}

interface datePickiedForm {
  datePicked: number | string
}

interface ItemData {
  id: number;
  name: string;
  age: number;
  address: string;
}

interface HoverButtonData {
  hoverButton: string
}

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.scss']
})
export class ScheduleComponent {
  dateFormat = 'dd MM yyyy';
  token$: any;

  constructor(private store: Store,
    private renderer: Renderer2,
    private elementRef: ElementRef,
    private apiService: ApiService,
    private notification: NzNotificationService) {
    this.token$ = store.select(tokenData);
  }

  form: SearchForm = { search: '' }

  datePicked: datePickiedForm = { datePicked: Date.now() }

  specialEndDate = Date.now();

  receivedMessage = ''

  placement = 'topLeft';

  schedule: ScheduleItems = {
    employeeId: '',
    employeeFullName: '',
    employeeTitle: ''
  }

  onDateIncrement() {
    this.datePicked.datePicked = moment(this.datePicked.datePicked).add(1, 'day').format('yyyy-MM-DD')
  }

  onDateDecrement() {
    this.datePicked.datePicked = moment(this.datePicked.datePicked).subtract(1, 'day').format('yyyy-MM-DD')
  }

  onDateToday() {
    this.datePicked.datePicked = Date.now()
  }

  initialproducts = this.schedule;

  SuccessNotification(type: string, position: NzNotificationPlacement): void {
    this.notification.create(
      type,
      this.receivedMessage, '',
      { nzPlacement: position }
    );
  }

  LoginFailedNotification(type: string, position: NzNotificationPlacement): void {
    this.notification.create(
      type,
      this.receivedMessage, '',
      { nzPlacement: position }
    );
  }

  GridJs: any[] = [];
  TableData: any[] = [];
  column = Object.keys(this.TableData[0] || {});

  ScheduleHeaderTableData: any[] = [];

  ngAfterViewInit() {
    this.renderer.setStyle(this.elementRef.nativeElement.ownerDocument.body, 'background-color', 'white');
  }

  fetchSchedule() {
    this.apiService.get('/api/auth/schedule/employees')
      .then(response => {
        this.ScheduleHeaderTableData = response;
      })
      .catch(error => console.log(error));
  }

  ngOnInit(): void {
    this.fetchSchedule()
    // console.log(this.datePicked.datePicked)
  }

  onValueChange() {
    this.TableData = GridJs.filter((data: any) =>
      this.column.some((column) =>
        data[column] === null ? "" : data[column].toString()
          .toLowerCase().indexOf(this.form.search.toString().toLowerCase()) > -1
      )
    )
  }
  listOfCurrentPageData: readonly ItemData[] = [];

  onCurrentPageDataChange($event: readonly ItemData[]): void {
    this.listOfCurrentPageData = $event;
  }
}
