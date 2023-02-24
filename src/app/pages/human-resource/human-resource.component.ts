import {
  Component, OnInit, TemplateRef, ChangeDetectionStrategy,
  ViewEncapsulation,
} from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NzDrawerPlacement } from 'ng-zorro-antd/drawer';
import { ApiService } from 'src/app/core/services/api.service';
import { Store } from '@ngrx/store';
import { tokenData } from 'src/app/reducers/token/token.reducer';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';

import {
  CalendarEvent,
  CalendarMonthViewDay,
  CalendarView,
  CalendarWeekViewBeforeRenderEvent,
} from 'angular-calendar';
import { WeekViewHour, WeekViewHourColumn } from 'calendar-utils';


interface EmployeeForm {
  userId: string,
  firstName: string,
  lastName: string,
  password: string,
  clearPassword: string,
  employeeType: string,
  employeeLevel: string,
  mustBookConsultationFirstTime: boolean,
  contactDetails: {
    emailAddress: string,
    mobileNumber: string
  },
  userPermissions: {
    analytics: {
      canRead: boolean,
      canWrite: boolean
    },
    bookings: {
      canRead: boolean,
      canWrite: boolean
    },
    clients: {
      canRead: boolean,
      canWrite: boolean
    },
    configurations: {
      canRead: boolean,
      canWrite: boolean
    },
    employees: {
      canRead: boolean,
      canWrite: boolean
    },
    marketing: {
      canRead: boolean,
      canWrite: boolean
    },
    reporting: {
      canRead: boolean,
      canWrite: boolean
    },
    sales: {
      canRead: boolean,
      canWrite: boolean
    },
    schedule: {
      canRead: boolean,
      canWrite: boolean
    },
    treatments: {
      canRead: boolean,
      canWrite: boolean
    },
    products: {
      canRead: boolean,
      canWrite: boolean
    },
    goals: {
      canRead: boolean,
      canWrite: boolean
    }
  }
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
  selector: 'app-human-resource',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './human-resource.component.html',
  styleUrls: ['./human-resource.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class HumanResourceComponent implements OnInit {
  dateFormat = 'dd MM yyyy';

  form: SearchForm = { search: '' }

  employee: EmployeeForm = {
    userId: "",
    firstName: "",
    lastName: "",
    password: "",
    clearPassword: "",
    employeeType: "",
    employeeLevel: '',
    mustBookConsultationFirstTime: false,
    contactDetails: {
      emailAddress: '',
      mobileNumber: ''
    },
    userPermissions: {
      analytics: {
        canRead: false,
        canWrite: false
      },
      bookings: {
        canRead: false,
        canWrite: false
      },
      clients: {
        canRead: false,
        canWrite: false
      },
      configurations: {
        canRead: false,
        canWrite: false
      },
      employees: {
        canRead: false,
        canWrite: false
      },
      marketing: {
        canRead: false,
        canWrite: false
      },
      reporting: {
        canRead: false,
        canWrite: false
      },
      sales: {
        canRead: false,
        canWrite: false
      },
      schedule: {
        canRead: false,
        canWrite: false
      },
      treatments: {
        canRead: false,
        canWrite: false
      },
      products: {
        canRead: false,
        canWrite: false
      },
      goals: {
        canRead: false,
        canWrite: false
      }
    }
  }

  initialemployee = this.employee;

  reintialiseEmployee() {
    this.employee = this.initialemployee;
  }

  employeeLevelSelect(data: string): void {
    this.employee.employeeLevel = data;
  }

  employeeFilterType: string = 'Filter by Employe Type'
  employeeFilterTypeSelect(data: string): void {
    this.employeeFilterType = data;
    this.handleTableDataFilter()
  }
  employeeFilterlevel: string = 'Filter by Employe level'
  employeeFilterLevelSelect(data: string): void {
    this.employeeFilterlevel = data;
    this.handleTableDataFilter()
  }
  employeeFilterStatus: string = 'Filter by Status'
  employeeFilterStatusSelect(data: string): void {
    this.employeeFilterStatus = data;
    this.handleTableDataFilter()
  }

  GridJs: any[] = [];
  TableData: any[] = [];
  column = Object.keys(this.TableData[0] || {});

  GoalTableData: any[] = [
    {
      id: 1,
      date: "2023-02-21",
      amount: 100,
      final_amount: 90,
    },
    {
      id: 2,
      date: "2023-02-22",
      amount: 140,
      final_amount: 120,
    },
    {
      id: 3,
      date: "2023-02-22",
      amount: 300,
      final_amount: 370,
    },
  ];

  onValueChange() {
    this.TableData = this.TableData.filter((data: any) =>
      this.column.some((column) =>
        data[column] === null ? "" : data[column].toString()
          .toLowerCase().indexOf(this.form.search.toString().toLowerCase()) > -1
      )
    )
  }

  handleTableDataFilter() {
    if (this.employeeFilterType !== 'Filter by Employe Type' &&
      this.employeeFilterlevel !== 'Filter by Employe level' &&
      this.employeeFilterStatus !== 'Filter by Status') {
      this.TableData = this.GridJs.filter(
        (data: { employeeType: string; }) => data.employeeType === this.employeeFilterType).filter(
          (data: { employeeLevel: string; }) => data.employeeLevel === this.employeeFilterlevel).filter(
            (data: { active: boolean; }) => data.active === (this.employeeFilterStatus === 'ACTIVE' ? true : false))
    }
    else if (this.employeeFilterType === 'Filter by Employe Type' &&
      this.employeeFilterlevel !== 'Filter by Employe level' &&
      this.employeeFilterStatus !== 'Filter by Status') {
      this.TableData = this.GridJs.filter(
        (data: { employeeLevel: string; }) => data.employeeLevel === this.employeeFilterlevel).filter(
          (data: { active: boolean; }) => data.active === (this.employeeFilterStatus === 'ACTIVE' ? true : false))
    }
    else if (this.employeeFilterType !== 'Filter by Employe Type' &&
      this.employeeFilterlevel === 'Filter by Employe level' &&
      this.employeeFilterStatus !== 'Filter by Status') {
      this.TableData = this.GridJs.filter(
        (data: { employeeType: string; }) => data.employeeType === this.employeeFilterType).filter(
          (data: { active: boolean; }) => data.active === (this.employeeFilterStatus === 'ACTIVE' ? true : false))
    }
    else if (this.employeeFilterType !== 'Filter by Employe Type' &&
      this.employeeFilterlevel !== 'Filter by Employe level' &&
      this.employeeFilterStatus === 'Filter by Status') {
      this.TableData = this.GridJs.filter(
        (data: { employeeType: string; }) => data.employeeType === this.employeeFilterType).filter(
          (data: { employeeLevel: string; }) => data.employeeLevel === this.employeeFilterlevel)
    }
    else if (this.employeeFilterType === 'Filter by Employe Type' &&
      this.employeeFilterlevel === 'Filter by Employe level' &&
      this.employeeFilterStatus !== 'Filter by Status') {
      this.TableData = this.GridJs.filter(
        (data: { active: boolean; }) => data.active === (this.employeeFilterStatus === 'ACTIVE' ? true : false))
    }
    else if (this.employeeFilterType === 'Filter by Employe Type' &&
      this.employeeFilterlevel !== 'Filter by Employe level' &&
      this.employeeFilterStatus === 'Filter by Status') {
      this.TableData = this.GridJs.filter(
        (data: { employeeLevel: string; }) => data.employeeLevel === this.employeeFilterlevel)
    }
    else if (this.employeeFilterType !== 'Filter by Employe Type' &&
      this.employeeFilterlevel === 'Filter by Employe level' &&
      this.employeeFilterStatus === 'Filter by Status') {
      this.TableData = this.GridJs.filter(
        (data: { employeeType: string; }) => data.employeeType === this.employeeFilterType)
    }
    else if (this.employeeFilterType === 'Filter by Employe Type' &&
      this.employeeFilterlevel === 'Filter by Employe level' &&
      this.employeeFilterStatus === 'Filter by Status') {
      this.TableData = this.GridJs
    }
  }

  constructor(
    private modalService: NgbModal,
    private offcanvasService: NgbOffcanvas,
    private apiService: ApiService,
    private notification: NzNotificationService) {
  }

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

  selectedEmployeeId: string = '';
  isEmployeeEnabled: boolean = false;

  employeeEditSelect(data: string): void {
    this.selectedEmployeeId = data;
    this.apiService.get(`/api/auth/employees/${this.selectedEmployeeId}`)
      .then(response => {
        this.isEmployeeEnabled = response.active
        this.employee = {
          userId: response.userId,
          firstName: response.firstName,
          lastName: response.lastName,
          password: '',
          clearPassword: '',
          employeeType: response.employeeType,
          employeeLevel: response.employeeLevel,
          mustBookConsultationFirstTime: response.mustBookConsultationFirstTime,
          contactDetails: {
            emailAddress: response.contactDetails.emailAddress,
            mobileNumber: response.contactDetails.mobileNumber
          },
          userPermissions: {
            analytics: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Analytics")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Analytics")[0].canWrite
            },
            bookings: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Bookings")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Bookings")[0].canWrite
            },
            clients: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Clients")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Clients")[0].canWrite
            },
            configurations: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Configurations")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Configurations")[0].canWrite
            },
            employees: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Employees")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Employees")[0].canWrite
            },
            marketing: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Marketing")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Marketing")[0].canWrite
            },
            reporting: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Reporting")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Reporting")[0].canWrite
            },
            sales: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Sales")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Sales")[0].canWrite
            },
            schedule: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Schedule")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Schedule")[0].canWrite
            },
            treatments: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Treatments")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Treatments")[0].canWrite
            },
            products: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Products")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Products")[0].canWrite
            },
            goals: {
              canRead: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Goals")[0].canRead,
              canWrite: response.userPermissionList.filter((data: { permissionFeature: string; }) => data.permissionFeature === "Goals")[0].canWrite
            }
          }
        }
        document.getElementById('open-employee-modal')?.click()
      })
      .catch(error => console.log(error));
  }

  onEmployeeEditSubmit() {
    this.apiService.put('/api/auth/employees', this.employee)
      .then(response => {
        this.receivedMessage = 'Employee updated successfuly'
        setTimeout(() => {
          this.SuccessNotification('success', 'topLeft')
        }, 200)
        setTimeout(() => {
          document.getElementById('close-employee-modal')?.click()
          this.selectedEmployeeId = '';
          this.isEmployeeEnabled = false;
          this.employee = this.initialemployee;
        }, 600)
        this.fetchEmployees()
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

  onEmployeeDisableSubmit() {
    if (this.isEmployeeEnabled === true) {
      this.apiService.put(`/api/auth/employees/disable/${this.selectedEmployeeId}`, {})
        .then(response => {
          this.isEmployeeEnabled = false;
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topLeft')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-employee-modal')?.click()
            this.selectedEmployeeId = '';
            this.isEmployeeEnabled = false;
            this.employee = this.initialemployee;
          }, 600)
          this.fetchEmployees()
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
      this.apiService.put(`/api/auth/employees/enable/${this.selectedEmployeeId}`, {})
        .then(response => {
          this.isEmployeeEnabled = false;
          this.receivedMessage = response.message
          setTimeout(() => {
            this.SuccessNotification('success', 'topLeft')
          }, 200)
          setTimeout(() => {
            document.getElementById('close-employee-modal')?.click()
            this.selectedEmployeeId = '';
            this.isEmployeeEnabled = false;
            this.employee = this.initialemployee;
          }, 600)
          this.fetchEmployees()
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

  onEmployeeSubmit() {
    if (this.employee.firstName && this.employee.lastName &&
      this.employee.password && this.employee.clearPassword &&
      this.employee.employeeType && this.employee.employeeLevel &&
      this.employee.contactDetails.emailAddress &&
      this.employee.contactDetails.mobileNumber !== '') {
      if (this.employee.password === this.employee.clearPassword) {
        this.apiService.post('/api/auth/employees', this.employee)
          .then(response => {
            this.receivedMessage = 'Employee created successfuly'
            setTimeout(() => {
              this.SuccessNotification('success', 'topLeft')
            }, 200)
            setTimeout(() => {
              document.getElementById('close-employee-modal')?.click()
              this.employee = this.initialemployee;
            }, 600)
            this.fetchEmployees()
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
        this.receivedMessage = 'Passwords are not matching retry!'
        setTimeout(() => {
          this.LoginFailedNotification('error', 'topLeft')
        }, 200)
      }

    }
    else {
      this.receivedMessage = 'Missing form details'
      setTimeout(() => {
        this.LoginFailedNotification('error', 'topLeft')
      }, 200)
    }
  }

  fetchEmployees() {
    this.apiService.get('/api/auth/employees')
      .then(response => {
        this.GridJs = response;
        this.TableData = [...this.GridJs];
        this.column = Object.keys(this.GridJs[0] || {});

        this.apiService.get('/api/auth/employees/employee-types')
          .then(response => {
            this.employeesTypesData = response;
          })
          .catch(error => console.log(error));
      })
      .catch(error => console.log(error));
  }

  employeesTypesData: any = [];

  // fetchEmployeesTypes() {
  //   this.apiService.get('/api/auth/employees/employee-types')
  //     .then(response => {
  //       this.employeesTypesData = response;
  //     })
  //     .catch(error => console.log(error));
  // }

  ngOnInit() {
    this.fetchEmployees()
  }

  visible = false;

  open(): void {
    this.visible = true;
  }

  close(): void {
    this.visible = false;
  }

  openEnd(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { position: 'end' });
  }

  selectedOption = 1
  options = [
    { value: 'option1', label: 'Option 1' },
    { value: 'option2', label: 'Option 2' },
  ]

  hovered = false;
  selectedIndex: number | null = null;
  activeIndex: number | null = 0;
  menuItems = [
    { id: 1, title: 'Employee details', icon: 'ri-contacts-line' },
    { id: 2, title: 'Working hours', icon: 'ri-timer-line' },
    { id: 3, title: 'Goals', icon: 'ri-donut-chart-fill' }]

  fullXLModal(fullXlDataModal: any) {
    this.modalService.open(fullXlDataModal, { size: 'xl', centered: true, backdrop: 'static' });
  }

  listOfCurrentPageData: readonly ItemData[] = [];

  onCurrentPageDataChange($event: readonly ItemData[]): void {
    this.listOfCurrentPageData = $event;
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





  view: CalendarView = CalendarView.Month;

  viewDate: Date = new Date();

  selectedMonthViewDay!: CalendarMonthViewDay;

  selectedDayViewDate!: Date;

  hourColumns!: WeekViewHourColumn[];

  events: CalendarEvent[] = [];

  selectedDays: any = [];

  dayClicked(day: CalendarMonthViewDay): void {
    this.selectedMonthViewDay = day;
    const selectedDateTime = this.selectedMonthViewDay.date.getTime();
    const dateIndex = this.selectedDays.findIndex(
      (selectedDay: { date: { getTime: () => number; }; }) => selectedDay.date.getTime() === selectedDateTime
    );
    if (dateIndex > -1) {
      delete this.selectedMonthViewDay.cssClass;
      this.selectedDays.splice(dateIndex, 1);
    } else {
      this.selectedDays.push(this.selectedMonthViewDay);
      day.cssClass = 'cal-day-selected';
      this.selectedMonthViewDay = day;
    }

    console.log(Date.now())
  }

  beforeMonthViewRender({ body }: { body: CalendarMonthViewDay[] }): void {
    body.forEach((day) => {
      if (
        this.selectedDays.some(
          (selectedDay: { date: { getTime: () => number; }; }) => selectedDay.date.getTime() === day.date.getTime()
        )
      ) {
        day.cssClass = 'cal-day-selected';
      }
    });
  }

  hourSegmentClicked(date: Date) {
    this.selectedDayViewDate = date;
    this.addSelectedDayViewClass();
  }

  beforeWeekOrDayViewRender(event: CalendarWeekViewBeforeRenderEvent) {
    this.hourColumns = event.hourColumns;
    this.addSelectedDayViewClass();
  }

  private addSelectedDayViewClass() {
    this.hourColumns.forEach((column) => {
      column.hours.forEach((hourSegment) => {
        hourSegment.segments.forEach((segment) => {
          delete segment.cssClass;
          if (
            this.selectedDayViewDate &&
            segment.date.getTime() === this.selectedDayViewDate.getTime()
          ) {
            segment.cssClass = 'cal-day-selected';
          }
        });
      });
    });
  }
}
