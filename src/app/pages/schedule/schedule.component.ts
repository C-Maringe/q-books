import { Component, ElementRef, Renderer2 } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Store } from '@ngrx/store';
import * as moment from 'moment';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';
import { tokenData } from 'src/app/reducers/token/token.reducer';

interface BlockOutTime {
  blockoutTimeTitle: string,
  startDateTime: string,
  endDateTime: string,
  employees: [
    string
  ]
}

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

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.scss']
})
export class ScheduleComponent {
  dateFormat = 'dd MM yyyy';
  token$: any;

  constructor(
    private modalService: NgbModal,
    private store: Store,
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

  blockOutTimeDateSelected: any = ''

  blockOutTime: BlockOutTime = {
    blockoutTimeTitle: '',
    startDateTime: '',
    endDateTime: '',
    employees: ['']
  }

  onBlockOutTime() {
    this.blockOutTime.startDateTime = moment(this.blockOutTimeDateSelected).format("YYYY-MM-DD") + " " + this.blockOutTime.startDateTime
    this.blockOutTime.endDateTime = moment(this.blockOutTimeDateSelected).format("YYYY-MM-DD") + " " + this.blockOutTime.endDateTime
    if (this.blockOutTime.blockoutTimeTitle && this.blockOutTime.startDateTime
      && this.blockOutTime.endDateTime && this.blockOutTime.employees[0] &&
      this.blockOutTimeDateSelected !== '') {
      this.apiService.post('/api/auth/schedule/blockout-time', this.blockOutTime)
        .then(response => {
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-Schedule-Blockout-modal')?.click()
            this.blockOutTime = {
              blockoutTimeTitle: '',
              startDateTime: '',
              endDateTime: '',
              employees: ['']
            }
          }, 600)
        })
        .catch(error => {
          setTimeout(() => {
            this.LoginFailedNotification('error', 'topRight')
          }, 200)
          if (error.error.detail === undefined) {
            this.receivedMessage = 'Server Error'
          }
          else {
            this.receivedMessage = error.error.detail
          }
        })
    }
    else {
      this.receivedMessage = 'Form is missing some values, please input values'
      setTimeout(() => {
        this.LoginFailedNotification('error', 'topRight')
      }, 200)
    }
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
  BookingScheduleTableData: any[] = [];
  EmployeeScheduleTableData: any[] = [];

  EmployeeScheduleSelectValue = {
    employeeFullName: '',
    employeeId: '',
    employeeTitle: ''
  }

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

  fetchBookingSchedule() {
    this.apiService.get('/api/auth/schedule/booking-cancellation')
      .then(response => {
        this.BookingScheduleTableData = response;
      })
      .catch(error => console.log(error));
  }

  fetchEmployeeSchedule() {
    this.apiService.get('/api/auth/schedule/employees')
      .then(response => {
        this.EmployeeScheduleTableData = response;
      })
      .catch(error => console.log(error));
  }

  ngOnInit(): void {
    this.fetchSchedule();
    this.fetchBookingSchedule();
    this.fetchEmployeeSchedule();
    // console.log(this.datePicked.datePicked)
  }

  onValueChange() {
    this.TableData = this.GridJs.filter((data: any) =>
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

  fullXLModal(fullXlDataModal: any) {
    this.modalService.open(fullXlDataModal, { size: 'xl', centered: true, backdrop: 'static' });
  }

  signupModal(signupDataModal: any) {
    this.modalService.open(signupDataModal, { centered: true });
  }
}