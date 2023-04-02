import { Component, ElementRef, Renderer2 } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Store } from '@ngrx/store';
import * as moment from 'moment';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';
import { tokenData } from 'src/app/reducers/token/token.reducer';
import { DropdownModule } from 'primeng/dropdown';

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
  countries: any[] = [
    { name: 'Australia', code: 'AU' },
    { name: 'Brazil', code: 'BR' },
    { name: 'China', code: 'CN' },
    { name: 'Egypt', code: 'EG' },
    { name: 'France', code: 'FR' },
    { name: 'Germany', code: 'DE' },
    { name: 'India', code: 'IN' },
    { name: 'Japan', code: 'JP' },
    { name: 'Spain', code: 'ES' },
    { name: 'United States', code: 'US' }
  ];

  selectedCountry = { name: '', code: '', time: '' }



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
            this.FailedNotification('error', 'topRight')
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
        this.FailedNotification('error', 'topRight')
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

  FailedNotification(type: string, position: NzNotificationPlacement): void {
    this.notification.create(
      type,
      this.receivedMessage, '',
      { nzPlacement: position }
    );
  }

  GridJs: any[] = [];
  TableData: any[] = [];
  column = Object.keys(this.TableData[0] || {});

  ScheduleClientTableData: any[] = [];
  BookingScheduleTableData: any[] = [];
  EmployeeScheduleTableData: any[] = [];
  TimeTableData: any[] = [];
  TreatmentsTableDate: any[] = [];

  EmployeeScheduleSelectValue = {
    employeeFullName: '',
    employeeId: '',
    employeeTitle: ''
  }

  SelectedDateModal: any = ''

  selectedEmployeeModal = {
    employeeFullName: "",
    employeeId: "",
    employeeTitle: ""
  }
  selectedClientModal = {
    clientFullName: "",
    clientId: ""
  }

  selectedTimeModal = {
    time: "",
    available: false
  }

  ngAfterViewInit() {
    this.renderer.setStyle(this.elementRef.nativeElement.ownerDocument.body, 'background-color', 'white');
  }

  fetchTreatmentsSchedule() {
    this.apiService.get('/api/auth/sales/treatments')
      .then(response => {
        this.TreatmentsTableDate = response;
        this.TreatmentsTableDate = this.TreatmentsTableDate.map(({ ...rest }, index) => ({ ...rest, index: index + 1, times: 0 }));
      })
      .catch(error => console.log(error));
  }

  SubtractTimes(data: any) {
    if (data.times <= 0) {
      this.receivedMessage = 'Times Cant be less than 1'
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
    }
    else {
      this.TreatmentsTableDate = this.TreatmentsTableDate.map((data_map) => {
        return data_map.treatmentId === data.treatmentId ? {
          ...data_map, times: data.times - 1
        } : data_map
      })
    }
  }

  AddTimes(data: any) {
    this.TreatmentsTableDate = this.TreatmentsTableDate.map((data_map) => {
      return data_map.treatmentId === data.treatmentId ? {
        ...data_map, times: data.times + 1
      } : data_map
    })
  }

  onBookNow() {
    var data = {
      startDateTime: moment(this.SelectedDateModal).format('YYYY-M-D') + " " + this.selectedTimeModal.time,
      employeeId: this.selectedEmployeeModal.employeeId,
      clientId: this.selectedClientModal.clientId,
      scheduleNewBookingItemModels: this.TreatmentsTableDate.filter((data) => data.times !== 0).map((data) => ({
        id: data.treatmentId, quantity: data.times, specialOffer: data.special
      })),
      depositRequired: true
    }
    if (data.employeeId && data.clientId !== '' && data.startDateTime !== ' ') {
      this.apiService.post('/api/auth/schedule/employee-book', data)
        .then(response => {
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
          document.getElementById('close-booking-schedule-modal')?.click()
          this.TreatmentsTableDate = this.TreatmentsTableDate.map((data_map) => {
            return {
              ...data_map, times: 0
            }
          })
          this.SelectedDateModal = ''
          this.selectedEmployeeModal = { employeeFullName: "", employeeId: "", employeeTitle: "" }
          this.selectedClientModal = { clientFullName: "", clientId: "" }
          this.selectedTimeModal = { time: "", available: false }
        })
        .catch(error => {
          setTimeout(() => {
            this.FailedNotification('error', 'topRight')
          }, 200)
          if (error.error.detail === undefined) {
            this.receivedMessage = 'Server Error'
          }
          else {
            this.receivedMessage = error.error.detail
          }
        });
    }
    else {
      this.receivedMessage = 'Please select all fields to book'
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
    }

  }

  fetchTimeSchedule() {
    this.apiService.get('/api/auth/sales/times')
      .then(response => {
        this.TimeTableData = response;
      })
      .catch(error => console.log(error));
  }

  fetchClientSchedule() {
    this.apiService.get('/api/auth/schedule/clients')
      .then(response => {
        this.ScheduleClientTableData = response;
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

  fetchBookingSchedule() {
    this.apiService.get('/api/auth/schedule/booking-cancellation')
      .then(response => {
        this.BookingScheduleTableData = response;
      })
      .catch(error => console.log(error));
  }

  ngOnInit(): void {
    this.fetchClientSchedule();
    this.fetchBookingSchedule();
    this.fetchEmployeeSchedule();
    this.fetchTimeSchedule();
    this.fetchTreatmentsSchedule();
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
