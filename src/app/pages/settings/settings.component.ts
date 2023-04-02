import { Component } from '@angular/core';
import { NzNotificationPlacement, NzNotificationService } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

interface ConfigurationsSettings {
  id: string,
  sessionDurations: number | null,
  attendeesPerSession: number | null,
  cancelNotice: number | null,
  bookingNotice: number | null,
  workStartTime: string,
  workEndTime: string,
  availableWeekends: true,
  daysAvailable: any[],
  workingDays: any[],
  depositThreshold: number | null,
  depositPercentage: number | null
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent {

  constructor(
    private apiService: ApiService,
    private notification: NzNotificationService) {
  }

  configurationsSettings: ConfigurationsSettings = {
    id: '',
    sessionDurations: null,
    attendeesPerSession: null,
    cancelNotice: null,
    bookingNotice: null,
    workStartTime: '',
    workEndTime: '',
    availableWeekends: true,
    daysAvailable: [
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday",
      "Sunday"
    ],
    workingDays: [],
    depositThreshold: null,
    depositPercentage: null
  }

  workingDaysArray: any[] = []


  Monday = {
    workingDay: 'Monday',
    workStartTime: '',
    workEndTime: '',
    available: false
  }
  Tuesday = {
    workingDay: 'Tuesday',
    workStartTime: '',
    workEndTime: '',
    available: false
  }
  Wednesday = {
    workingDay: 'Wednesday',
    workStartTime: '',
    workEndTime: '',
    available: false
  }
  Thursday = {
    workingDay: 'Thursday',
    workStartTime: '',
    workEndTime: '',
    available: false
  }
  Friday = {
    workingDay: 'Friday',
    workStartTime: '',
    workEndTime: '',
    available: false
  }
  Saturday = {
    workingDay: 'Saturday',
    workStartTime: '',
    workEndTime: '',
    available: false
  }
  Sunday = {
    workingDay: 'Sunday',
    workStartTime: '',
    workEndTime: '',
    available: false
  }

  initialID = '';
  initialworkStartTime = '';
  initialworkEndTime = '';


  onUpdateSettings() {
    const upDateSettingsDat = {
      id: this.initialID,
      sessionDurations: this.configurationsSettings.sessionDurations,
      attendeesPerSession: this.configurationsSettings.attendeesPerSession,
      cancelNotice: this.configurationsSettings.cancelNotice,
      bookingNotice: this.configurationsSettings.bookingNotice,
      workStartTime: this.initialworkStartTime,
      workEndTime: this.initialworkEndTime,
      availableWeekends: true,
      daysAvailable: [
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
      ],
      workingDays: [
        this.Sunday,
        this.Monday,
        this.Tuesday,
        this.Wednesday,
        this.Thursday,
        this.Friday,
        this.Saturday
      ].filter((data) => data.available !== false),
      depositThreshold: this.configurationsSettings.depositThreshold,
      depositPercentage: this.configurationsSettings.depositPercentage
    }
    this.apiService.put('/api/auth/configurations', upDateSettingsDat)
      .then((response) => {
        this.receivedMessage = response.message
        setTimeout(() => {
          this.SuccessNotification('success', 'topRight')
        }, 200)
        setTimeout(() => {
          this.fetchApplicationConfigurations()
        }, 600)
      })
      .catch((error) => { console.log(error) })
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

  fetchApplicationConfigurations() {
    this.apiService.get('/api/auth/configurations')
      .then(response => {
        this.initialID = response.id
        this.configurationsSettings = response
        this.workingDaysArray = response.workingDays

        if (this.workingDaysArray.filter((data) => data.workingDay.includes('Monday')).length === 1) {
          this.Monday = {
            workingDay: this.workingDaysArray.filter((data) => data.workingDay.includes('Monday'))[0].workingDay,
            workStartTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Monday'))[0].workStartTime,
            workEndTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Monday'))[0].workEndTime,
            available: true
          }
        }
        if (this.workingDaysArray.filter((data) => data.workingDay.includes('Tuesday')).length === 1) {
          this.Tuesday = {
            workingDay: this.workingDaysArray.filter((data) => data.workingDay.includes('Tuesday'))[0].workingDay,
            workStartTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Tuesday'))[0].workStartTime,
            workEndTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Tuesday'))[0].workEndTime,
            available: true
          }
        }
        if (this.workingDaysArray.filter((data) => data.workingDay.includes('Wednesday')).length === 1) {
          this.Wednesday = {
            workingDay: this.workingDaysArray.filter((data) => data.workingDay.includes('Wednesday'))[0].workingDay,
            workStartTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Wednesday'))[0].workStartTime,
            workEndTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Wednesday'))[0].workEndTime,
            available: true
          }
        }
        if (this.workingDaysArray.filter((data) => data.workingDay.includes('Thursday')).length === 1) {
          this.Thursday = {
            workingDay: this.workingDaysArray.filter((data) => data.workingDay.includes('Thursday'))[0].workingDay,
            workStartTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Thursday'))[0].workStartTime,
            workEndTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Thursday'))[0].workEndTime,
            available: true
          }
        }
        if (this.workingDaysArray.filter((data) => data.workingDay.includes('Friday')).length === 1) {
          this.Friday = {
            workingDay: this.workingDaysArray.filter((data) => data.workingDay.includes('Friday'))[0].workingDay,
            workStartTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Friday'))[0].workStartTime,
            workEndTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Friday'))[0].workEndTime,
            available: true
          }
        }
        if (this.workingDaysArray.filter((data) => data.workingDay.includes('Saturday')).length === 1) {
          this.Saturday = {
            workingDay: this.workingDaysArray.filter((data) => data.workingDay.includes('Saturday'))[0].workingDay,
            workStartTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Saturday'))[0].workStartTime,
            workEndTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Saturday'))[0].workEndTime,
            available: true
          }
        }
        if (this.workingDaysArray.filter((data) => data.workingDay.includes('Sunday')).length === 1) {
          this.Sunday = {
            workingDay: this.workingDaysArray.filter((data) => data.workingDay.includes('Sunday'))[0].workingDay,
            workStartTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Sunday'))[0].workStartTime,
            workEndTime: this.workingDaysArray.filter((data) => data.workingDay.includes('Sunday'))[0].workEndTime,
            available: true
          }
        }

        // console.log(this.Sunday, this.Saturday, this.Friday, this.Thursday, this.Wednesday, this.Tuesday, this.Monday)

      })
      .catch(error => console.log(error));
  }

  ngOnInit() {
    this.fetchApplicationConfigurations()
  }
}
