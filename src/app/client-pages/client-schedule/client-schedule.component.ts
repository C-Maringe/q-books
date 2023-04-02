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

interface MobileCancellationQueue {
  startDate: string,
  endDate: string,
  startTime: string,
  endTime: string,
  employeeId: string
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
  selector: 'app-client-schedule',
  templateUrl: './client-schedule.component.html',
  styleUrls: ['./client-schedule.component.scss']
})
export class ClientScheduleComponent {
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

  placement = 'topRight';

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

  mobileCancellationQueue: MobileCancellationQueue = {
    startDate: '',
    endDate: '',
    startTime: '',
    endTime: '',
    employeeId: ''
  }

  selectedEmployeeForCancellation = ''

  onMobileCancellationQueueSubmit() {
    if (this.mobileCancellationQueue.startDate && this.mobileCancellationQueue.endDate &&
      this.mobileCancellationQueue.startTime && this.mobileCancellationQueue.endTime &&
      this.mobileCancellationQueue.employeeId !== '') {
      this.mobileCancellationQueue.startDate = moment(this.mobileCancellationQueue.startDate).format('yyyy-MM-DD')
      this.mobileCancellationQueue.endDate = moment(this.mobileCancellationQueue.endDate).format('yyyy-MM-DD')
      this.apiService.post('/api/mobile/booking-cancellation', this.mobileCancellationQueue)
        .then(response => {
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-client-schedule-cancellation-modal')?.click()
            this.selectedEmployeeForCancellation = ''
            this.mobileCancellationQueue = {
              startDate: '',
              endDate: '',
              startTime: '',
              endTime: '',
              employeeId: ''
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
      this.receivedMessage = `Please fill all fields`
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
    }
  }

  onDateIncrement() {
    this.datePicked.datePicked = moment(this.datePicked.datePicked).add(1, 'day').format('yyyy-MM-DD')
    this.DateOfTheWeek = moment(this.datePicked.datePicked).format('dddd')
    this.fetchEmployeeBookingSchedule()
  }

  onDateDecrement() {
    this.datePicked.datePicked = moment(this.datePicked.datePicked).subtract(1, 'day').format('yyyy-MM-DD')
    this.DateOfTheWeek = moment(this.datePicked.datePicked).format('dddd')
    this.fetchEmployeeBookingSchedule()
  }

  onDateToday() {
    this.datePicked.datePicked = Date.now()
    this.DateOfTheWeek = moment(this.datePicked.datePicked).format('dddd')
    this.fetchEmployeeBookingSchedule()
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

  ScheduleHeaderTableData: any[] = [];
  EmployeeBookingScheduleTableData: any[] = [];

  // sdfghjgfdsdghhgfdhjhgfdgjdsdfh





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

  selectedTimeModal = {
    time: "",
    available: false
  }

  ngAfterViewInit() {
    this.renderer.setStyle(this.elementRef.nativeElement.ownerDocument.body, 'background-color', 'white');
  }

  fetchTreatmentsSchedule() {
    this.apiService.get('/api/mobile/schedule/treatments')
      .then(response => {
        this.TreatmentsTableDate = response;
        this.TreatmentsTableDate = this.TreatmentsTableDate.map(({ ...rest }, index) => ({ ...rest, index: index + 1, times: 0 }));
      })
      .catch(error => console.log(error));
  }

  SubtractTimes(data: any) {
    if (data.times <= 0) {
      this.receivedMessage = 'Times Cant be less than 0'
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
      newBookingItemModel: this.TreatmentsTableDate.filter((data) => data.times !== 0).map((data) => ({
        id: data.treatmentId, quantity: data.times, specialOffer: data.special
      })),
      depositRequired: true
    }
    if (data.employeeId !== '' && data.startDateTime !== ' ') {
      this.apiService.post('/api/auth/schedule/client-book', data)
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
    if (this.SelectedDateModal && this.selectedEmployeeModal.employeeId !== '') {
      this.apiService.get(`/api/mobile/schedule/employees/${this.selectedEmployeeModal.employeeId}/${moment(this.SelectedDateModal).format('YYYY-M-D')}`)
        .then(response => {
          this.TimeTableData = response.filter((data: { available: boolean; }) => data.available !== false);
          console.log(this.TimeTableData)
        })
        .catch(error => console.log(error));
    }
  }

  onDateChanged() {
    setTimeout(() => {
      this.fetchTimeSchedule()
    }, 200)
  }




  // uikvcjkjhgrcvjkjfcvyuyhfcvjhdfvjhfj









  fetchSchedule() {
    this.apiService.get('/api/auth/schedule/employees')
      .then(response => {
        this.ScheduleHeaderTableData = response;
      })
      .catch(error => console.log(error));
  }

  fetchEmployeeBookingSchedule() {
    if (this.EmployeeScheduleSelectValue.employeeId !== '') {
      this.apiService.get(`/api/mobile/schedule/employees/${this.EmployeeScheduleSelectValue.employeeId}/${moment(this.datePicked.datePicked).format('yyyy-MM-DD')}`)
        .then(response => {
          this.EmployeeBookingScheduleTableData = response;
        })
        .catch(error => console.log(error));
    }
    else {
      this.receivedMessage = `Please select schedule to view availability`
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
    }
  }

  largeModal(largeDataModal: any) {
    this.modalService.open(largeDataModal, { size: 'lg', centered: true, backdrop: 'static' });
  }

  fetchEmployeeSchedule() {
    this.apiService.get('/api/auth/schedule/employees')
      .then(response => {
        this.EmployeeScheduleTableData = response;
      })
      .catch(error => console.log(error));
  }

  DateOfTheWeek: string = ''

  ngOnInit(): void {
    this.fetchSchedule();
    this.fetchEmployeeSchedule();
    this.fetchTreatmentsSchedule();
    this.DateOfTheWeek = moment(this.datePicked.datePicked).format('dddd')
  }

  DateOfTheWeekRun() {
    this.DateOfTheWeek = moment(this.datePicked.datePicked).format('dddd')
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
