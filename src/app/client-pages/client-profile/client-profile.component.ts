import { Component } from '@angular/core';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';
import * as moment from 'moment';

interface UserProfileInfoToupdate {
  firstName: string,
  lastName: string,
  emailAddress: string,
  mobileNumber: string
}

interface UserProfileInfo {
  id: string,
  firstName: string,
  lastName: string,
  emailAddress: string,
  mobileNumber: string,
  dateOfBirth: string,
  notes: any[],
  loyaltyPoints: number,
  vouchers: any[]
}

@Component({
  selector: 'app-client-profile',
  templateUrl: './client-profile.component.html',
  styleUrls: ['./client-profile.component.scss']
})
export class ClientProfileComponent {

  constructor(
    private apiService: ApiService,
    private notification: NzNotificationService) {
  }
  ActiveSelectedTab = 1

  userProfileInfoToupdate: UserProfileInfoToupdate = {
    firstName: '',
    lastName: '',
    emailAddress: '',
    mobileNumber: ''
  }

  userProfileInfo: UserProfileInfo = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
    mobileNumber: '',
    dateOfBirth: '',
    notes: [],
    loyaltyPoints: 0,
    vouchers: []
  }

  receivedMessage = ''

  placement = 'topRight';

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

  selectedDateRangeTab3: any = ''

  booking_statusData = 'All'
  booking_statusValue = 'All'

  BookingTableData: any = []

  moment = moment

  fetchUserInfo() {
    this.apiService.get('/api/mobile/profile')
      .then(response => {
        this.userProfileInfoToupdate.firstName = response.firstName
        this.userProfileInfoToupdate.lastName = response.lastName
        this.userProfileInfoToupdate.emailAddress = response.emailAddress
        this.userProfileInfoToupdate.mobileNumber = response.mobileNumber
        this.userProfileInfo = response
      })
      .catch(error => console.log(error));
  }

  fetchBookingsInfo() {
    this.apiService.get('/api/mobile/bookings')
      .then(response => {
        this.BookingTableData = response
      })
      .catch(error => console.log(error));
  }

  onUpdateUserInfo() {
    if (this.userProfileInfoToupdate.firstName && this.userProfileInfoToupdate.firstName &&
      this.userProfileInfoToupdate.firstName && this.userProfileInfoToupdate.firstName !== '') {
      this.apiService.put('/api/mobile/profile', this.userProfileInfoToupdate)
        .then(response => {
          this.receivedMessage = 'User updated successfuly'
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
          setTimeout(() => {
            this.fetchUserInfo()
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
      this.receivedMessage = `One or more of the fields can${'`'}t be blank`
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
    }
  }

  ngOnInit() {
    this.fetchUserInfo()
    this.fetchBookingsInfo()
  }
}
