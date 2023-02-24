import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

interface ClientNotes {
  title: string,
  description: string
}

interface ClientsItems {
  id: string,
  firstName: string,
  lastName: string,
  emailAddress: string,
  mobileNumber: string,
  dateOfBirth: number,
  notes: [],
  loyaltyPoints: number,
  vouchers: [],
  active: boolean,
  password: string | undefined,
  confirmPassword: string | undefined
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
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.scss']
})
export class ClientsComponent implements OnInit {

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

  form: SearchForm = { search: '' }

  isClientEdit: boolean = false;

  clientsNotes: ClientNotes = {
    title: '',
    description: ''
  }

  handleNotesSubmit() {
    if (this.clientsNotes.title && this.clientsNotes.description !== '') {
      this.apiService.put(`/api/auth/clients/${this.clients.id}/notes`, this.clientsNotes)
        .then(response => {
          this.receivedMessage = 'Client notes updated successfuly'
          setTimeout(() => {
            this.SuccessNotification('success', 'topLeft')
          }, 200)
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
      this.receivedMessage = `title or description can't be blank`
      setTimeout(() => {
        this.LoginFailedNotification('error', 'topLeft')
      }, 200)
    }
  }

  clients: ClientsItems = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
    mobileNumber: '',
    dateOfBirth: Date.now(),
    notes: [],
    loyaltyPoints: 0,
    vouchers: [],
    active: false,
    password: undefined,
    confirmPassword: undefined
  }

  initialclients: ClientsItems = {
    id: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
    mobileNumber: '',
    dateOfBirth: Date.now(),
    notes: [],
    loyaltyPoints: 0,
    vouchers: [],
    active: false,
    password: undefined,
    confirmPassword: undefined
  }

  clientEditSelect(data: any): void {
    this.isClientEdit = true;

    console.log(data)

    this.clients.id = data.id;
    this.clients.firstName = data.firstName;
    this.clients.lastName = data.lastName;
    this.clients.emailAddress = data.emailAddress;
    this.clients.mobileNumber = data.mobileNumber;
    this.clients.dateOfBirth = data.dateOfBirth;
    this.clients.notes = data.notes;
    this.clients.loyaltyPoints = data.loyaltyPoints;
    this.clients.vouchers = data.vouchers;
    this.clients.active = data.active;

    document.getElementById('open-client-modal')?.click()
  }

  GridJs: any[] = [];
  TableData: any[] = [];
  column = Object.keys(this.TableData[0] || {});

  fetchClients() {
    this.apiService.get('/api/auth/clients')
      .then(response => {
        this.GridJs = response;
        this.TableData = [...this.GridJs];
        this.column = Object.keys(this.GridJs[0] || {});
      })
      .catch(error => console.log(error));
  }

  onCancelModal() {
    this.isClientEdit = false;
    this.clients = this.initialclients;
    this.clientsNotes = {
      title: '',
      description: ''
    }
  }

  onClientUpdate() {
    this.apiService.put(`/api/auth/clients/${this.clients.id}`, this.clients)
      .then(response => {
        this.receivedMessage = 'Client account updated successfuly'
        setTimeout(() => {
          this.SuccessNotification('success', 'topLeft')
        }, 200)
        setTimeout(() => {
          document.getElementById('close-clients-modal')?.click()
          this.clients.active = false;
          this.clients = this.initialclients;
        }, 600)
        this.fetchClients()
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

  onClientDisable() {
    if (this.clients.active === true) {
      this.apiService.put(`/api/auth/clients/${this.clients.id}/disable`, {})
        .then(response => {
          this.clients.active = false;
          this.clients = this.initialclients;
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topLeft')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-clients-modal')?.click()
            this.clients.active = false;
          }, 600)
          this.fetchClients()
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
      this.apiService.put(`/api/auth/clients/${this.clients.id}/enable`, {})
        .then(response => {
          this.clients.active = false;
          this.clients = this.initialclients;
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topLeft')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-clients-modal')?.click()
            this.clients.active = false;
          }, 600)
          this.fetchClients()
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

  onSubmitClientFunction() {
    this.apiService.post('/api/public/web/register', this.clients)
      .then(response => {
        this.receivedMessage = 'Client created successfuly'
        setTimeout(() => {
          this.SuccessNotification('success', 'topLeft')
        }, 200)
        setTimeout(() => {
          document.getElementById('close-clients-modal')?.click()
          this.clients = this.initialclients;
        }, 600)
        this.fetchClients()
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

  dateOfBirth = Date.now()

  onClientSubmit() {
    const myDate = new Date(this.dateOfBirth)
    this.clients.dateOfBirth = myDate.getTime();
    if (this.clients.firstName && this.clients.lastName &&
      this.clients.mobileNumber && this.clients.emailAddress !== '') {
      if (this.clients.password && this.clients.password !== undefined) {
        if (this.clients.password === this.clients.password) {
          this.onSubmitClientFunction()
        }
        else {
          this.receivedMessage = 'Passwords Entered are not matching'
          setTimeout(() => {
            this.LoginFailedNotification('error', 'topLeft')
          }, 200)
        }
      }
      else {
        this.receivedMessage = `Passwords can't be blank`
        setTimeout(() => {
          this.LoginFailedNotification('error', 'topLeft')
        }, 200)
      }
    }
    else {
      this.receivedMessage = 'Missing some form details'
      setTimeout(() => {
        this.LoginFailedNotification('error', 'topLeft')
      }, 200)
    }
  }

  ngOnInit() { this.fetchClients() }

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
