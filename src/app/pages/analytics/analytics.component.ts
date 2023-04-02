import { Component } from '@angular/core';
import { NzNotificationService, NzNotificationPlacement } from 'ng-zorro-antd/notification';
import { ApiService } from 'src/app/core/services/api.service';

// Series Data
export const series = {
  monthDataSeries1: {
    prices: ["0", "3", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"],
    dates: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
  }
}

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent {

  constructor(
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

  totalBookingsToDate = 0
  totalClientsToDate = 0
  totalWorkedToDate = 0

  tileBoxs1: any[] = []

  async fetchCardsData() {
    try {
      await this.apiService.get('/api/auth/analytics/totalBookingsToDate')
        .then(response => { this.totalBookingsToDate = response.totalBookings })
        .catch((error) => console.log(error))
      await this.apiService.get('/api/auth/analytics/totalClientsToDate')
        .then(response => { this.totalClientsToDate = response.totalClients })
        .catch((error) => console.log(error))
      await this.apiService.get('/api/auth/analytics/totalWorkedToDate')
        .then(response => { this.totalWorkedToDate = response.totalWorkDone })
        .catch((error) => console.log(error))
      this.tileBoxs1 = [{
        id: 1,
        label: "Total Clients To Date",
        labelClass: "muted",
        percentage: "+16.24 %",
        percentageClass: "success",
        percentageIcon: "ri-arrow-right-up-line",
        counter: this.totalClientsToDate,
        caption: "View net earnings",
        icon: "bx bx-dollar-circle",
        iconClass: "success",
        decimals: 0,
        prefix: "",
        suffix: "",
      },
      {
        id: 2,
        label: "Total Bookings To Date",
        labelClass: "muted",
        percentage: "+16.24 %",
        percentageClass: "success",
        percentageIcon: "ri-arrow-right-up-line",
        counter: this.totalBookingsToDate,
        caption: "View net earnings",
        icon: "bx bx-dollar-circle",
        iconClass: "success",
        decimals: 0,
        prefix: "",
        suffix: "",
      },
      {
        id: 3,
        label: "Total Work Done (Hours)",
        labelClass: "muted",
        percentage: "+29.08 %",
        percentageClass: "success",
        percentageIcon: "ri-arrow-right-up-line",
        counter: this.totalWorkedToDate,
        caption: "See details",
        icon: "bx bx-user-circle",
        iconClass: "warning",
        decimals: 0,
        prefix: "",
        suffix: "h",
      }]
    }
    catch {

    }
  }

  ColumnWithRotatedChart: any;

  ngOnInit() {
    this.fetchCardsData()

    this._ColumnWithRotatedChart('["--vz-info"]');
    this._basicAreaChart('["--vz-success"]');
  }

  private getChartColorsArray(colors: any) {
    colors = JSON.parse(colors);
    return colors.map(function (value: any) {
      var newValue = value.replace(" ", "");
      if (newValue.indexOf(",") === -1) {
        var color = getComputedStyle(document.documentElement).getPropertyValue(newValue);
        if (color) {
          color = color.replace(" ", "");
          return color;
        }
        else return newValue;;
      } else {
        var val = value.split(',');
        if (val.length == 2) {
          var rgbaColor = getComputedStyle(document.documentElement).getPropertyValue(val[0]);
          rgbaColor = "rgba(" + rgbaColor + "," + val[1] + ")";
          return rgbaColor;
        } else {
          return newValue;
        }
      }
    });
  }

  labelsValues: any[] = []
  datasetsValues: any[] = []

  private _ColumnWithRotatedChart(colors: any) {
    colors = this.getChartColorsArray(colors);
    this.apiService.get('/api/auth/analytics/signups')
      .then(response => {
        this.labelsValues = response.labels
        this.datasetsValues = response.datasets[0].data
        console.log(response)
      })
      .catch((error) => console.log(error))
    this.ColumnWithRotatedChart = {
      series: [{
        name: "Servings",
        data: ["5", "2", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"],
      },],
      chart: {
        height: 350,
        type: "bar",
        toolbar: {
          show: false,
        },
      },
      plotOptions: {
        bar: {
          borderRadius: 10,
          columnWidth: "50%",
        },
      },
      dataLabels: {
        enabled: false,
      },
      stroke: {
        width: 2,
      },
      xaxis: {
        labels: {
          rotate: -45,
        },
        categories: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
        tickPlacement: "on",
      },
      yaxis: {
        title: {
          text: "Total sign ups",
        },
      },
      fill: {
        type: "gradient",
        gradient: {
          shade: "light",
          type: "horizontal",
          shadeIntensity: 0.25,
          gradientToColors: undefined,
          inverseColors: true,
          opacityFrom: 0.85,
          opacityTo: 0.85,
          stops: [0, 0, 100],
        },
      },
      colors: '#21D59B'
    };
  }

  basicAreaChart: any;

  private _basicAreaChart(colors: any) {
    colors = this.getChartColorsArray(colors);
    this.basicAreaChart = {
      series: [
        {
          name: "Month",
          data: series.monthDataSeries1.prices
        }
      ],
      chart: {
        type: "area",
        height: 350,
        zoom: {
          enabled: false
        }
      },
      dataLabels: {
        enabled: false
      },
      stroke: {
        curve: "straight"
      },
      title: {
        text: " Bookings / Month",
        align: "left"
      },
      labels: series.monthDataSeries1.dates,
      // xaxis: {
      //   type: "datetime"
      // },
      yaxis: {
        opposite: true
      },
      legend: {
        horizontalAlign: "right"
      },
      colors: ['#21D59B'],
    };
  }

}
