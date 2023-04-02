import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

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
  selector: 'app-client-services',
  templateUrl: './client-services.component.html',
  styleUrls: ['./client-services.component.scss']
})
export class ClientServicesComponent {


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

  FailedNotification(type: string, position: NzNotificationPlacement): void {
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

  special_date = Date.now()

  isServiceEdit = false;

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
    this.apiService.get('/api/mobile/schedule/all-treatments')
      .then(response => {
        this.GridJs = response;
        this.TableData = [...this.GridJs];
        this.column = Object.keys(this.GridJs[0] || {});
      })
      .catch(error => console.log(error));
  }

  onfetchEmployeeTypes() {
    this.apiService.get('/api/mobile/schedule/employee-types')
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
