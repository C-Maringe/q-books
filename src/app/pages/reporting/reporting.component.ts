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
  selector: 'app-reporting',
  templateUrl: './reporting.component.html',
  styleUrls: ['./reporting.component.scss']
})
export class ReportingComponent {

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

  selectedDateRangeTab1: any = ''
  topClientsBookingReport: any[] = []

  handleTab1Submit() {
    var url = `/api/auth/reporting/topBookedClient/${moment(this.selectedDateRangeTab1.from).valueOf()}/${moment(this.selectedDateRangeTab1.to).add(1, 'days').valueOf()}`
    this.apiService.get(url)
      .then(response => {
        this.topClientsBookingReport = response
        this.receivedMessage = response.length > 0 ? 'Top clients Bookings successfully retrieved' : 'There are no client bookings for the selected period'
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
      })
      .catch(error => console.log(error));
  }

  selectedDateRangeTab2: any = ''
  topServicesBookingReport: any[] = []

  handleTab2Submit() {
    var url = `/api/auth/reporting/topBookedServiceItem/${moment(this.selectedDateRangeTab2.from).valueOf()}/${moment(this.selectedDateRangeTab2.to).add(1, 'days').valueOf()}`
    this.apiService.get(url)
      .then(response => {
        console.log(response)
        this.topServicesBookingReport = response
        this.receivedMessage = response.length > 0 ? 'Top Services bookings report successfully retrieved' : 'There are no service bookings for the selected period'
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
      })
      .catch(error => console.log(error));
  }

  selectedDateRangeTab3: any = ''

  booking_statusData = 'All'
  booking_statusValue = 'All'
  ClientsTableData3: any[] = []
  ClientSelectValue3 = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
  }
  ClientSelectValueInitial = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: 'All',
  }
  optionClientSelectValue3 = ''

  EmployessTableData: any[] = []
  EmployessSelectValue3 = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
  }
  EmployessSelectValueInitial = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: 'All',
  }
  optionEmployessSelectValue3 = ''

  getTopServiceItemsPerClient3: any[] = []

  handleTab3Submit() {
    if (this.selectedDateRangeTab3 && this.ClientSelectValue3.emailAddress && this.EmployessSelectValue3.emailAddress !== '') {
      var url = `/api/auth/reporting/bookings/` +
        `${moment(this.selectedDateRangeTab3.from).format('YYYY-M-D')}/` +
        `${moment(this.selectedDateRangeTab3.to).add(1, 'days').format('YYYY-M-D')}` +
        `/${this.booking_statusData}/${this.ClientSelectValue3.emailAddress}/${this.EmployessSelectValue3.emailAddress}`
      this.apiService.get(url)
        .then(response => {
          console.log(response)
          this.receivedMessage = 'Successfully retrieved bookings'
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
        })
        .catch(error => console.log(error));
    }
    else {
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
      this.receivedMessage = 'Please fill in all details'
    }
  }

  selectedDateRangeTab4: any = ''
  topSignupsReport = {
    totalActiveClients: 0,
    totalClientsWithBookings: 0
  }

  topSignupsReportArray: any = []

  moment = moment

  handleTab4Submit() {
    var url = `/api/auth/reporting/signups/${moment(this.selectedDateRangeTab4.from).format('YYYY-M-D')}/${moment(this.selectedDateRangeTab4.to).add(1, 'days').format('YYYY-M-D')}`
    this.apiService.get(url)
      .then(response => {
        this.topSignupsReportArray = response.reportClientSignUpModels
        this.topSignupsReport.totalActiveClients = response.totalActiveClients
        this.topSignupsReport.totalClientsWithBookings = response.totalClientsWithBookings
        this.receivedMessage = response.reportClientSignUpModels.length > 0 ? 'Top Services bookings report successfully retrieved' : 'There are no service bookings for the selected period'
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
      })
      .catch(error => console.log(error));
  }

  ClientsTableData: any[] = []
  ClientSelectValue = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
  }

  optionClientSelectValue = ''

  getTopServiceItemsPerClient: any[] = []

  handleTab5Submit() {
    if (this.ClientSelectValue.emailAddress !== '') {
      this.apiService.get(`/api/auth/reporting/getTopServiceItemsPerClient/${this.ClientSelectValue.emailAddress}`)
        .then(response => {
          this.getTopServiceItemsPerClient = response
          this.receivedMessage = response.length > 0 ? 'Top Services items per client successfully retrieved' : 'There are no service items for the selected client'
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
        })
        .catch(error => console.log(error));
    }
    else {
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
      this.receivedMessage = 'Please select client'
    }
  }

  handleFetchClients() {
    this.apiService.get('/api/auth/reporting/clientsFilter')
      .then(response => {
        this.ClientsTableData = response
        this.ClientsTableData3 = response
        this.receivedMessage = 'Successfully retrieved clients List'
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
      })
      .catch(error => console.log(error));
  }

  handleFetchEmployess() {
    this.apiService.get('/api/auth/reporting/employeesFilter')
      .then(response => {
        this.EmployessTableData = response
        this.receivedMessage = 'Successfully retrieved employees List'
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
      })
      .catch(error => console.log(error));
  }

  ngOnInit() {
    this.handleFetchClients()
    this.handleFetchEmployess()
  }
}
