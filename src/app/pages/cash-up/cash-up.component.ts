import { Component } from '@angular/core';
import * as moment from 'moment';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

@Component({
  selector: 'app-cash-up',
  templateUrl: './cash-up.component.html',
  styleUrls: ['./cash-up.component.scss']
})
export class CashUpComponent {

  constructor(
    private apiService: ApiService,
    private notification: NzNotificationService) {
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

  selectedDate = ''

  cashUpStartData: any[] = []

  CashUpSummedData = {
    overviewTotal: 0,
    cashupAlreadyStarted: false,
    cashupCanBeCompleted: false,
    cashupId: ''
  }

  handleSearchSales() {
    var url = `/api/auth/sales/cashup-start/${moment(this.selectedDate).format('YYYY-M-D')}`
    this.apiService.put(url, {})
      .then(response => {
        this.cashUpStartData = response.salesBookingModelList;
        this.CashUpSummedData = {
          overviewTotal: response.overviewTotal,
          cashupAlreadyStarted: response.cashupAlreadyStarted,
          cashupCanBeCompleted: response.cashupCanBeCompleted,
          cashupId: response.cashupId
        }
        this.receivedMessage = response.salesBookingModelList.length === 0 ? 'There are no cash up sales for the selected date' : 'Cash Up sales successfully retrieved for the selected date'
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
      })
      .catch(error => console.log(error));
  }
}
