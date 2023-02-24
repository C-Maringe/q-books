import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';
import * as moment from 'moment';

interface ServicesItems {
  treatmentName: string,
  treatmentDescription: string,
  seniorPrice: number | string,
  special: boolean,
  specialEndDate: string,
  specialPrice: number | string,
  duration: number | string,
  employeeType: string,
  active: boolean,
  treatmentId: string,
  doneByJunior: boolean,
  doneBySenior: boolean,
  juniorPrice: number | string
}

interface SearchForm {
  search: string
}

interface ItemData {
  id: number;
  name: string;
  age: number;
  address: string;
}

@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.scss']
})
export class ServicesComponent {


  constructor(
    private modalService: NgbModal,
    private apiService: ApiService,
    private notification: NzNotificationService) { }

  receivedMessage = ''

  placement = 'topLeft';

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


  services: ServicesItems = {
    treatmentName: '',
    treatmentDescription: '',
    seniorPrice: '',
    special: false,
    specialEndDate: '',
    specialPrice: '',
    duration: '',
    employeeType: '',
    active: false,
    treatmentId: '',
    doneByJunior: false,
    doneBySenior: false,
    juniorPrice: ''
  }

  initialservices = {
    treatmentName: '',
    treatmentDescription: '',
    seniorPrice: '',
    special: false,
    specialEndDate: '',
    specialPrice: '',
    duration: '',
    employeeType: '',
    active: false,
    treatmentId: '',
    doneByJunior: false,
    doneBySenior: false,
    juniorPrice: ''
  }

  special_date = Date.now()

  isServiceEdit = false;

  onCancelModal() {
    this.isServiceEdit = false;
    this.services = this.initialservices;
  }

  servicesEditSelect(data: any): void {
    this.isServiceEdit = true;
    this.services = data
    document.getElementById('open-services-modal')?.click()
  }

  onSubmitClientFunction() {
    this.apiService.post('/api/auth/treatments', this.services)
      .then(response => {
        this.receivedMessage = response.message
        setTimeout(() => {
          this.SuccessNotification('success', 'topLeft')
        }, 200)
        setTimeout(() => {
          document.getElementById('close-services-modal')?.click()
          this.services = this.initialservices;
        }, 600)
        this.fetchServices()
      })
      .catch(error => {
        setTimeout(() => {
          this.LoginFailedNotification('error', 'topLeft')
        }, 200)
        if (error.error.detail === undefined) {
          this.receivedMessage = 'Server Error'
        }
        else {
          this.receivedMessage = error.error.detail
        }
      });
  }

  onServiceSubmit() {
    this.services.specialEndDate = moment(this.special_date).format("yyyy-M-D");
    if (this.services.treatmentName && this.services.treatmentDescription
      && this.services.employeeType && this.services.seniorPrice
      && this.services.duration !== '') {
      if (this.services.special) {
        if (this.services.specialPrice && this.services.specialEndDate !== '') {
          this.onSubmitClientFunction()
        }
        else {
          this.receivedMessage = 'Special fields are missing values'
          setTimeout(() => {
            this.LoginFailedNotification('error', 'topLeft')
          }, 200)
        }
      }
      else {
        this.onSubmitClientFunction()
      }

    }
    else {
      this.receivedMessage = 'Missing some form details'
      setTimeout(() => {
        this.LoginFailedNotification('error', 'topLeft')
      }, 200)
    }
  }

  onServicesUpdate() {
    this.services.specialEndDate = moment(this.special_date).format("yyyy-M-D");
    this.apiService.put(`/api/auth/treatments`, this.services)
      .then(response => {
        this.receivedMessage = response.message
        setTimeout(() => {
          this.SuccessNotification('success', 'topLeft')
        }, 200)
        setTimeout(() => {
          document.getElementById('close-services-modal')?.click()
          this.onCancelModal()
        }, 600)
        this.fetchServices()
      })
      .catch(error => {
        setTimeout(() => {
          this.LoginFailedNotification('error', 'topLeft')
        }, 200)
        if (error.error.detail === undefined) {
          this.receivedMessage = 'Server Error'
        }
        else {
          this.receivedMessage = error.error.detail
        }
      });
  }

  onServicesDisable() {
    if (this.services.active === true) {
      this.apiService.put(`/api/auth/treatments/disable/${this.services.treatmentId}`, {})
        .then(response => {
          this.services.active = false;
          this.services = this.initialservices;
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topLeft')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-services-modal')?.click()
            this.services.active = false;
          }, 600)
          this.fetchServices()
        })
        .catch(error => {
          setTimeout(() => {
            this.LoginFailedNotification('error', 'topLeft')
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
      this.apiService.put(`/api/auth/treatments/enable/${this.services.treatmentId}`, {})
        .then(response => {
          this.services.active = false;
          this.services = this.initialservices;
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topLeft')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-services-modal')?.click()
            this.services.active = false;
          }, 600)
          this.fetchServices()
        })
        .catch(error => {
          setTimeout(() => {
            this.LoginFailedNotification('error', 'topLeft')
          }, 200)
          if (error.error.detail === undefined) {
            this.receivedMessage = 'Server Error'
          }
          else {
            this.receivedMessage = error.error.detail
          }
        });
    }
  }

  form: SearchForm = { search: '' }

  GridJs: any[] = [];
  TableData: any[] = [];
  column = Object.keys(this.TableData[0] || {});
  employeeTypesData: any[] = []

  employeeFilterType: string = 'Filter by Employe Type'

  employeeFilterTypeSelect(data: string): void {
    this.employeeFilterType = data;
    this.handleTableDataFilter()
  }


  employeeFilterTypeform: string = ''

  employeeFilterTypeSelectForm(data: string): void {
    this.employeeFilterTypeform = data;
    this.services.employeeType = data
  }

  handleTableDataFilter() {
    this.TableData = this.GridJs.filter(
      (data: { employeeType: string; }) => data.employeeType === this.employeeFilterType)
  }

  fetchServices() {
    this.apiService.get('/api/auth/treatments')
      .then(response => {
        this.GridJs = response;
        this.TableData = [...this.GridJs];
        this.column = Object.keys(this.GridJs[0] || {});
      })
      .catch(error => console.log(error));
  }

  onfetchEmployeeTypes() {
    this.apiService.get('/api/auth/treatments/employee-types')
      .then(response => {
        this.employeeTypesData = response
      })
      .catch(error => console.log(error));
  }

  ngOnInit() {
    this.fetchServices();
    this.onfetchEmployeeTypes()
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

  fullLGModal(fullMdDataModal: any) {
    this.modalService.open(fullMdDataModal, { size: 'lg', centered: true, backdrop: 'static' });
  }

  isVisible = false;
  isOkLoading = false;

  showModal(): void {
    this.isVisible = true;
  }

  handleOk(): void {
    this.isOkLoading = true;
    setTimeout(() => {
      this.isVisible = false;
      this.isOkLoading = false;
    }, 3000);
  }

  handleCancel(): void {
    this.isVisible = false;
  }
}
