import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

interface ItemData {
  id: number;
  name: string;
  age: number;
  address: string;
}

@Component({
  selector: 'app-marketing',
  templateUrl: './marketing.component.html',
  styleUrls: ['./marketing.component.scss']
})
export class MarketingComponent {

  constructor(
    private modalService: NgbModal,
    private apiService: ApiService,
    private notification: NzNotificationService) {
  }

  receivedMessage = '';

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

  batchEmailsData: any[] = []

  async fetchData() {
    try {
      await this.apiService.get('/api/auth/marketing/batchEmails')
        .then(response => { this.batchEmailsData = response })
        .catch((error) => console.log(error))
    } catch {

    }
  }

  batchEmailsDataTableData: any[] = []
  currentBatchEmailId = ''

  async fetchBatchEmail(batchEmailId: any) {
    try {
      await this.apiService.get(`/api/auth/marketing/batchEmail/${batchEmailId}`)
        .then(response => {
          this.currentBatchEmailId = batchEmailId
          this.batchEmailsDataTableData = response.clients
        })
        .catch((error) => console.log(error))
    } catch {

    }
  }

  setToggleModalState: boolean = false;
  setToggleModalStateSendEmails: boolean = false;

  setupBatchList() {
    var JsonData = {
      clientIds: [...this.setOfCheckedId],
      batchEmailId: this.currentBatchEmailId
    }
    if ([...this.setOfCheckedId].length >= 1) {
      try {
        this.apiService.post(`/api/auth/marketing/setupBatchList`, JsonData)
          .then(response => {
            this.setToggleModalState = true
            this.batchEmailTitle = response.batchEmailTitle
            this.batchEmailMessage = response.batchEmailMessage
          })
          .catch((error) => console.log(error))
      } catch {

      }
    }
    else {
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
      this.receivedMessage = 'Please Select at least one client'
    }
  }

  batchEmailTitle = '';
  batchEmailMessage = ''

  saveEmailContent() {
    var JsonData = {
      batchEmailId: this.currentBatchEmailId,
      batchEmailTitle: this.batchEmailTitle,
      batchEmailMessage: this.batchEmailMessage
    }
    if (this.batchEmailTitle && this.batchEmailMessage !== '' && this.batchEmailTitle && this.batchEmailMessage !== 'No Content') {
      try {
        this.apiService.put(`/api/auth/marketing/setupBatchEmailContent`, JsonData)
          .then(response => {
            this.fetchData()
            this.setToggleModalStateSendEmails = true
            this.receivedMessage = 'Emails title and message successfully updated click send emails button to procced sending emails'
            setTimeout(() => {
              this.SuccessNotification('success', 'topRight')
            }, 200)
          })
          .catch((error) => console.log(error))
      } catch {

      }
    }
    else {
      setTimeout(() => {
        this.FailedNotification('error', 'topRight')
      }, 200)
      this.receivedMessage = 'Invalid email title or message entered'
    }
  }

  sendBatchEmail() {
    try {
      this.apiService.put(`/api/auth/marketing/sendBatchEmail/${this.currentBatchEmailId}`, {})
        .then(response => {
          this.fetchData()
          document.getElementById('close-marketing-modal')?.click()
          this.setToggleModalStateSendEmails = false
          this.batchEmailTitle = ''
          this.batchEmailMessage = ''
          this.receivedMessage = 'Emails successfully sent'
          setTimeout(() => {
            this.SuccessNotification('success', 'topRight')
          }, 200)
        })
        .catch((error) => console.log(error))
    } catch {

    }
  }


  fullXLModal(fullXlDataModal: any) {
    this.modalService.open(fullXlDataModal, { size: 'xl', centered: true, backdrop: 'static' });
  }

  ngOnInit() {
    this.fetchData()
  }

  checked = false;
  indeterminate = false;
  listOfCurrentPageData: readonly ItemData[] = [];
  listOfData: readonly ItemData[] = [];
  setOfCheckedId = new Set<number>();

  updateCheckedSet(id: number, checked: boolean): void {
    if (checked) {
      this.setOfCheckedId.add(id);
    } else {
      this.setOfCheckedId.delete(id);
    }
  }

  onItemChecked(id: number, checked: boolean): void {
    this.updateCheckedSet(id, checked);
    this.refreshCheckedStatus();
  }

  onAllChecked(value: boolean): void {
    this.batchEmailsDataTableData.forEach(item => {
      this.updateCheckedSet(item.userId, value)
    });
    this.refreshCheckedStatus();
  }

  refreshCheckedStatus(): void {
    this.checked = this.listOfCurrentPageData.every(item => this.setOfCheckedId.has(item.id));
    this.indeterminate = this.listOfCurrentPageData.some(item => this.setOfCheckedId.has(item.id)) && !this.checked;
  }
}


