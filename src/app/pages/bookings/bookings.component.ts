import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

interface SearchForm {
  search: string
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
  selector: 'app-bookings',
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.scss']
})
export class BookingsComponent {

  dateFormat = 'dd-MM-yyyy';
  buttonform: HoverButtonData = { hoverButton: 'Get Bookings' }
  toggleButtonform1() { this.buttonform.hoverButton = "BlockOut Time" }
  toggleButtonform2() { this.buttonform.hoverButton = "Get Bookings" }

  constructor(
    private modalService: NgbModal,
    private apiService: ApiService,
    private notification: NzNotificationService) { }

  receivedMessage = ''

  placement = 'topRight';

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

  form: SearchForm = { search: '' }

  GridJs: any[] = [];
  TableData: any[] = [];
  column = Object.keys(this.TableData[0] || {});

  bookingsDateSelected = Date.now();
  bookingSelectedDatePost: string | undefined

  fetchBookings() {
    this.bookingSelectedDatePost = moment(this.bookingsDateSelected).format('yyyy-M-D')
    this.apiService.get(`/api/auth/bookings/${this.bookingSelectedDatePost}`)
      .then(response => {
        this.GridJs = response.map((item: { bookingSlot: string; }) => {
          return { ...item, bookingSlot: [item.bookingSlot.split(" ")[1], item.bookingSlot.split(" ")[4]].join(" - ") };
        });
        this.TableData = [...this.GridJs];
        this.column = Object.keys(this.GridJs[0] || {});

        this.receivedMessage = `Successfuly loaded bookings for ${this.bookingSelectedDatePost}`
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
      })
      .catch(error => console.log(error));
  }

  onNotifyOneBooking(data: any): void {
    this.bookingSelectedDatePost = moment(this.bookingsDateSelected).format('yyyy-M-D')
    this.apiService.put(`/api/auth/bookings/notify`, {
      date: this.bookingSelectedDatePost,
      bookingId: data.bookingId
    })
      .then(response => {
        this.receivedMessage = response.message
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
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
      });
  }

  onCancelNotifyOneBooking(data: any): void {
    this.apiService.put(`/api/auth/bookings/${data.bookingId}/cancel`, {})
      .then(response => {
        this.receivedMessage = response.message
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
        setTimeout(() => {
          this.fetchBookings()
        }, 300)
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
      });
  }

  onNotifyAllForDate() {
    this.bookingSelectedDatePost = moment(this.bookingsDateSelected).format('yyyy-M-D')
    if (this.GridJs.length > 0) {
      this.apiService.put(`/api/auth/bookings/notify/${this.bookingSelectedDatePost}`, {})
        .then(response => {
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
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
        });
    }
    else {
      this.receivedMessage = 'There are no bookings yet for the selected date'
      setTimeout(() => {
        this.LoginFailedNotification('error', 'topRight')
      }, 200)
    }

  }

  handleNewSelectedDate() {
    this.fetchBookings()
  }

  ngOnInit() { this.fetchBookings() }

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

}
