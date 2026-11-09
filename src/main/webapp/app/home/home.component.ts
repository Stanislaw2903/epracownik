import { ToastService } from './../shared/toast/toast.service';
import { HomeService } from './home.service';
import { IVacationApplication, IVacationFullDayDict } from './home.interface';
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Observable, Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { HttpResponse } from '@angular/common/http';
import { INotification, Notification } from 'app/shared/model/notification.model';
import { NotificationService } from 'app/entities/notification/notification.service';
import { IUser } from 'app/core/user/user.model';
import * as moment from 'moment';
import { MenuItem, SelectItem } from 'primeng/api';
import { IVacationDetailData, IVacationFreeDays, IVacationHistoryDetails } from 'app/shared/model/vacations.model';
import { ChartComponent, ApexNonAxisChartSeries, ApexPlotOptions, ApexChart } from 'ng-apexcharts';
import { TranslateService } from '@ngx-translate/core';
import { switchMap, tap } from 'rxjs/operators';
export type ChartOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  labels: string[];
  plotOptions: ApexPlotOptions;
};
@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  @ViewChild('chart') chart: ChartComponent | undefined;
  public chartOptions: any;

  vacationTypesDict: SelectItem[] = [];

  account: Account | null = null;
  authSubscription?: Subscription;
  data: any;
  notificationDisplay = false;
  notificationReply = false;
  notificationData: INotification | null = null;
  notificationReplyData = '';
  vacation = {
    data: {},
    options: {
      legend: {
        display: false,
      },
    },
  };
  options: any;
  leave: number;
  notifications: any[];
  vacationFreeDays!: IVacationFreeDays;

  vacationDaysChart: any = {
    data: {},
    options: {
      legend: {
        display: false,
      },
    },
  };

  itemsMenuModel: MenuItem[] = [];
  vacationItemsMenuModel: MenuItem[] = [];
  currentYear: Date = moment().toDate();
  isVacationDetailsDialogVisible = false;
  vacationDetails: IVacationHistoryDetails[] = [];

  // Vacation details data
  vacationDetailsData!: IVacationDetailData;

  // Vacation application data
  isVacationApplicationVisible = false;
  vacationApplicationModel: IVacationApplication = {
    fullDay: null,
    vacationType: null,
    comment: '',
  };
  fullDayDict: IVacationFullDayDict[] = [];
  preselectFullDay = true;

  // User vacation
  isUserVacationVisible = false;
  activeVacationButton = true;
  showTooltipActiveProposal = false;
  minDate: Date = new Date();
  vacationSubmitting = false;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    protected notificationService: NotificationService,
    private homeService: HomeService,
    private _toastService: ToastService,
    private translateService: TranslateService
  ) {
    this.homeService.canActiveVacationButton().subscribe(canPlaceProposal => {
      this.activeVacationButton = canPlaceProposal === 'ACTIVE' || canPlaceProposal === '';
      this.showTooltipActiveProposal = canPlaceProposal === 'ACTIVE_PROPOSAL';
    });
    this.homeService.getVacationTypes().subscribe((types: any) => {
      this.vacationTypesDict = types.map((type: any) => {
        return { label: this.translateService.instant('ebokApp.vacations.types.' + type), value: type };
      });
    });
    this.chartOptions = {
      series: [70, 30],
      chart: {
        height: 200,
        type: 'donut',
      },
      legend: {
        show: false,
      },
      dataLabels: {
        enabled: false,
      },
      colors: ['#dc3545', '#2daa48'],
      responsive: [
        {
          breakpoint: 2500,
          options: {
            chart: {
              height: 200,
            },
          },
        },
        {
          breakpoint: 1740,
          options: {
            chart: {
              height: 150,
            },
          },
        },
        {
          breakpoint: 1199,
          options: {
            chart: {
              height: 250,
            },
          },
        },
        {
          breakpoint: 1400,
          options: {
            chart: {
              height: 150,
            },
          },
        },
      ],
      plotOptions: {
        pie: {
          donut: {
            labels: {
              show: true,
              total: {
                showAlways: true,
                show: true,
                label: 'Dni',
                formatter: (w: any) =>
                  w.globals.seriesTotals.reduce((a: any, b: any) => {
                    return b;
                  }, 0),
              },
            },
          },
        },
      },
      labels: ['Dni wykorzystane', 'Dni do wykorzystania'],
    };
    this.fullDayDict = homeService.getFullDayDict();

    this.leave = 12;
    this.notifications = [];
    this.data = {
      labels: ['Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec', 'Lipiec'],
      datasets: [
        {
          label: 'Choroba',
          backgroundColor: '#dc354539',
          borderColor: '#dc3545',
          data: [5, 0, 2, 10, 0, 5, 2],
        },
        {
          label: 'Urlop wypoczynkowy',
          borderColor: '#2e3541',
          fill: false,
          data: [0, 0, 15, 5, 1, 0, 5],
        },
      ],
    };

    this.options = {
      scales: {
        yAxes: [
          {
            display: false,
          },
        ],
      },
    };

    this.vacation.data = {
      labels: ['Dni wykorzystane', 'Dni do wykorzystania'],
      datasets: [
        {
          data: [12, 16],
          backgroundColor: ['#dc3545', '#161c25', '#FFCE56'],
          hoverBackgroundColor: ['#FF6384', '#2e3541', '#161c25'],
        },
      ],
    };

    this.prepareVacationHistoryChartData();

    this.vacationItemsMenuModel = [
      {
        label: 'Moje wnioski urlopowe',
        icon: 'pi pi-calendar',
        routerLink: ['/dashboard/user-vacation'],
      },
    ];

    this.itemsMenuModel = [
      {
        label: 'Poprzedni rok',
        icon: 'pi pi-angle-double-left',
        command: () => {
          this.prepareDateForVacationHistory();
          this.prepareVacationHistoryChartData();
        },
      },
      {
        label: 'Aktualny rok',
        icon: 'pi pi-angle-double-down',
        command: () => {
          this.prepareDateForVacationHistory(true);
          this.prepareVacationHistoryChartData();
        },
      },
      {
        label: 'Historia urlopów',
        icon: 'pi pi-info-circle',
        command: () => {
          this.isVacationDetailsDialogVisible = true;
          this.homeService.getVacationDaysDetails(moment(this.currentYear).format('YYYY')).subscribe(data => {
            this.vacationDetails = data;
          });
        },
      },
    ];
  }

  prepareVacationHistoryChartData(): void {
    this.homeService.getVacationDaysChartData(moment(this.currentYear).format('YYYY')).subscribe((data: number[]) => {
      this.vacationDaysChart.data = this.homeService.getVacationDaysChart();
      this.vacationDaysChart.data.datasets = [];
      this.vacationDaysChart.data.datasets.push({
        data: data.map(monthUsedHours => monthUsedHours / 8),
        backgroundColor: '#dc3545',
      });
    });
  }

  prepareDateForVacationHistory(resetToCurrentYear = false): void {
    if (resetToCurrentYear) {
      this.currentYear = moment().toDate();
    } else {
      this.currentYear = moment(this.currentYear).subtract(1, 'year').toDate();
    }
  }

  prepareMonthDetailtForDisplay(vacationDetail: IVacationHistoryDetails): Date {
    return moment()
      .set('month', vacationDetail.month - 1)
      .toDate();
  }

  handlerChangeVacationHistoryDate(isPreviosYear = false): void {
    if (isPreviosYear) {
      this.prepareDateForVacationHistory();
      this.homeService.getVacationDaysDetails(moment(this.currentYear).format('YYYY')).subscribe(data => {
        this.vacationDetails = data;
      });
    } else {
      this.prepareDateForVacationHistory(true);
      this.homeService.getVacationDaysDetails(moment(this.currentYear).format('YYYY')).subscribe(data => {
        this.vacationDetails = data;
      });
    }
  }

  getFreeDays(): void {
    if (this.vacationApplicationModel.dateFrom && this.vacationApplicationModel.dateTo) {
      this.homeService
        .getFreeVacationDays(this.vacationApplicationModel.dateFrom, this.vacationApplicationModel.dateTo)
        .subscribe((data: IVacationFreeDays) => {
          this.vacationFreeDays = data;
        });
    }
  }

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    const pageToLoad = 1;
    this.notificationService
      .userNotifications({
        page: pageToLoad - 1,
        size: 20,
        sort: ['status', 'createDate,desc'],
      })
      .subscribe(
        (res: HttpResponse<INotification[]>) => this.onSuccess(res.body),
        () => this.onError()
      );

    this.homeService.getVacationData().subscribe(data => {
      this.vacationDetailsData = data.body as IVacationDetailData;

      this.vacationDetailsData.total = this.vacationDetailsData?.used + this.vacationDetailsData?.vested;
      if (this.vacationDetailsData?.vested) {
        this.vacationDetailsData.percentage = (this.vacationDetailsData.vested * 100) / this.vacationDetailsData.total;
      }
      this.chartOptions.series = [this.vacationDetailsData.used, this.vacationDetailsData.vested];
    });
  }

  prepareVacationKnobClass(): string {
    return `p${this.vacationDetailsData?.percentage || 0}`;
  }

  changeCategory(event: any): void {
    this.vacationApplicationModel.fullDay = event.value;
    if (event.value === true) {
      this.vacationApplicationModel.vacationType = 'ANNUAL_LEAVE';
    }
  }

  setDateToAsSameLikeDateFrom(): void {
    this.vacationApplicationModel.dateTo = this.vacationApplicationModel.dateFrom;
    this.getFreeDays();
  }

  protected onSuccess(data: INotification[] | null): void {
    this.notifications = data || [];
  }

  protected onError(): void {
    // this.ngbPaginationPage = this.page ?? 1;
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  getNotification(id: number, notification: INotification): void {
    notification.status = true;
    this.notificationService.findForUser(id).subscribe(
      (res: HttpResponse<INotification>) => this.showNotification(res.body),
      () => this.onError()
    );
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotification>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.notificationReply = false;
    this.notificationData = null;
  }

  protected onSaveError(): void {}

  sendReply(): void {
    const replyNotification = this.createFromForm(this.notificationData as INotification);
    this.subscribeToSaveResponse(this.notificationService.create(replyNotification));
  }

  private createFromForm(notification: INotification): INotification {
    return {
      ...new Notification(),
      subject: 'Re: ' + notification.subject,
      description: notification.description,
      createDate: moment(),
      category: notification.category,
      recepients: [notification.createdBy as IUser],
    };
  }

  showNotification(data: INotification | null): void {
    this.notificationData = data;
    this.notificationDisplay = true;
  }

  showNotificationReply(data: INotification | null): void {
    this.notificationData = { ...data };
    this.notificationData.description = '';
    this.notificationReply = true;
  }

  openVacationApplicationModal(): void {
    this.isVacationApplicationVisible = true;
    this.changeCategory({ value: true });
  }

  sendVacationApplication(isFormValid: boolean): void {
    if (!isFormValid || this.vacationSubmitting) {
      return;
    }
    this.vacationSubmitting = true;
    this.vacationApplicationModel.used = this.vacationFreeDays.workingDays;
    this.homeService
      .sendVacationApplication(this.vacationApplicationModel)
      .pipe(
        switchMap(() => {
          return this.homeService.canActiveVacationButton().pipe(
            tap(canPlaceProposal => {
              this.activeVacationButton = canPlaceProposal === 'ACTIVE';
              this.showTooltipActiveProposal = canPlaceProposal === 'ACTIVE_PROPOSAL';
            })
          );
        })
      )
      .subscribe(
        () => {
          this.vacationSubmitting = false;
          this.vacationApplicationModel = {
            fullDay: null,
            dateFrom: moment().toDate(),
            dateTo: moment().toDate(),
            vacationType: null,
            comment: '',
          };

          this.isVacationApplicationVisible = false;

          this._toastService.sendMessage({
            type: 'success',
            title: 'home.vacationApplication.title',
            message: 'home.vacationApplication.success',
          });
        },
        error => {
          this.vacationSubmitting = false;
          if (error.status === 418) {
            this._toastService.sendMessage({
              type: 'error',
              title: 'home.vacationApplication.title',
              message: 'home.vacationApplication.vacationTypeError',
            });
          } else if (error.status === 409) {
            const code = typeof error.error === 'string' ? error.error : '';
            const codeMap: { [key: string]: string } = {
              OVERLAP: 'home.vacationApplication.overlapError',
              PAST_DATE: 'home.vacationApplication.pastDateError',
              NOT_ENOUGH_DAYS: 'home.vacationApplication.notEnoughDaysError',
              TOO_MANY_ON_REQUEST: 'home.vacationApplication.tooManyOnRequestError',
            };
            this._toastService.sendMessage({
              type: 'error',
              title: 'home.vacationApplication.title',
              message: codeMap[code] || 'home.vacationApplication.error',
            });
          } else {
            this._toastService.sendMessage({
              type: 'error',
              title: 'home.vacationApplication.title',
              message: 'home.vacationApplication.error',
            });
          }
        }
      );
  }

  changeVacationDateWithNoFullDay(): void {
    if (!this.vacationApplicationModel.fullDay) {
      this.vacationApplicationModel.dateTo = this.vacationApplicationModel.dateFrom;
    }
  }

  prepareTimeRange(date: Date, type: string): Date {
    const nextDayDiffHours = moment.duration(moment(date).endOf('day').diff(moment(date)));
    let preparedDate;
    if (type === 'min') {
      if (nextDayDiffHours.asHours() > 8) {
        preparedDate = moment(date).subtract(Math.floor(nextDayDiffHours.asHours()), 'hours').toDate();
      } else {
        preparedDate = moment(date).subtract(8, 'hours').toDate();
      }
    } else {
      if (nextDayDiffHours.asHours() < 8) {
        preparedDate = moment(date).add(Math.floor(nextDayDiffHours.asHours()), 'hours').toDate();
      } else {
        preparedDate = moment(date).add(8, 'hours').toDate();
      }
    }
    return preparedDate;
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
