import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { get } from 'lodash';
import * as moment from 'moment';
import { MenuItem, Message, MessageService } from 'primeng/api';
import { ToastService } from 'app/shared/toast/toast.service';
import { IEmployeeIncomeContent } from '../employee-income.interface';
import { IDocumentPDFPayroll, IncomesService } from './../incomes.service';
import { Location } from '@angular/common';
import { pdfDefaultOptions } from 'ngx-extended-pdf-viewer';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'jhi-employee-income-card-payroll-table',
  templateUrl: './employee-income-card-payroll-table.component.html',
  styleUrls: ['./employee-income-card-payroll-table.component.scss'],
  providers: [MessageService],
})
export class EmployeeIncomeCardPayrollTableComponent implements OnInit {
  employeeIncome = Array<IEmployeeIncomeContent>();
  itemsMenuModel: MenuItem[] = [];
  total: any = {
    top: {
      fields: [
        'salaryInCash',
        'taxed',
        'taxedWithoutZus',
        'inability2',
        'socialSecurityContributionsPaid.retirement',
        'socialSecurityContributionsPaid.sickness',
        'costOfGettingIncome',
        'incomeInAGivenMonth',
        'incomeFromBeginningOfYear',
        'freeAmount',
        'healthInsurancePremium.taken',
        'advancePaymentForIncomeTax.due',
        'advancePaymentForIncomeTax.deductions',
      ],
      sum: [],
    },
    bottom: {
      fields: [
        'salaryInKind',
        'grossPremium',
        'withoutZusAndTax',
        'socialInsuranceBenefits',
        'socialSecurityContributionsPaid.disability',
        'socialSecurityContributionsPaid.contributionsSum',
        'socialSecurityDeducted',
        'rate',
        'accruedAdvancePaymentForIncomeTax',
        'healthInsurancePremium.deducted',
        'advancePaymentForIncomeTax.collected',
        'advancePaymentForIncomeTax.toPay',
      ],
      sum: [],
    },
  };

  currentYear: Date = moment().toDate();

  dialogVisibility = false;
  dropdownMenuVisibility = false;
  msgs: Message[] = [];
  fullScreenTable = false;

  // PDF
  isPDFPIT11ViewerDialogVisible = false;
  isBlockedDownloadPdfPIT11 = false;
  isPDFIMIRViewerDialogVisible = false;
  isBlockedDownloadPdfIMIR = false;
  pdfPIT11Src = '';
  pdfIMIRSrc = '';
  pdfData!: IDocumentPDFPayroll;
  isDisableConfirmReadPdf = false;

  get currentYearAsNumber(): number {
    return parseInt(moment(this.currentYear).format('YYYY'), 10);
  }

  constructor(
    private incomeService: IncomesService,
    private translate: TranslateService,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private _toastService: ToastService
  ) {
    // Konfiguracja PDF
    pdfDefaultOptions.assetsFolder = 'content';

    const year = this.route.snapshot.paramMap.get('payrollYear');

    if (year && moment(year, 'YYYY').isValid()) {
      this.currentYear = moment(year, 'YYYY').toDate();
    } else {
      this.router.navigate(['payroll/salaries', this.currentYearAsNumber]);
    }
  }

  ngOnInit(): void {
    this.getPayrollData(this.currentYear);
    this.setItemsMenuModel();
  }

  // Zwraca dane czy możliwe jest pobranie plików PDF
  getExistPdfInformation(): void {
    // Pobiera info o dostępności przycisków do pobierania PDF dla PIT11 i IMIR
    forkJoin([
      this.incomeService.getPDFDocumentExist('PIT_11', moment(this.currentYear).format('YYYY')),
      this.incomeService.getPDFDocumentExist('IMIR', moment(this.currentYear).format('YYYY')),
    ]).subscribe(result => {
      this.isBlockedDownloadPdfPIT11 = !result[0]; // Bo przychodzi true, czyli plik istnieje wiec nei blokuje
      this.isBlockedDownloadPdfIMIR = !result[1]; // Bo przychodzi true, czyli plik istnieje wiec nei blokuje
      this.setItemsMenuModel();
    });
  }

  // Potwierdza przeczytanie pliku pdf
  confirmDocumentPdf(): void {
    this.incomeService.setConfirmReadPdf(this.pdfData.id).subscribe(() => {
      this.isDisableConfirmReadPdf = true;
      this._toastService.sendMessage({
        type: 'success',
        title: 'payroll.toastReadPdf.title',
        message: 'payroll.toastReadPdf.summary',
      });
    });
  }

  setItemsMenuModel(): void {
    this.itemsMenuModel = [
      {
        label: 'Pokaż PIT11',
        icon: 'pi pi-file',
        disabled: this.isBlockedDownloadPdfPIT11,
        command: () => {
          this.incomeService.getFilePDF('PIT_11', moment(this.currentYear).format('YYYY')).subscribe(result => {
            this.pdfPIT11Src = result.file.content;
            this.isPDFPIT11ViewerDialogVisible = true;
            this.pdfData = result;
            this.isDisableConfirmReadPdf = !!result.confirmed;
          });
        },
      },
      {
        label: 'Pokaż IMIR',
        icon: 'pi pi-file',
        disabled: this.isBlockedDownloadPdfIMIR,
        command: () => {
          this.incomeService.getFilePDF('IMIR', moment(this.currentYear).format('YYYY')).subscribe(result => {
            this.pdfIMIRSrc = result.file.content;
            this.isPDFIMIRViewerDialogVisible = true;
            this.pdfData = result;
            this.isDisableConfirmReadPdf = !!result.confirmed;
          });
        },
      },
      {
        label: this.translate.instant('incomes.details'),
        icon: 'pi pi-info-circle',
        command: () => {
          this.dialogVisibility = true;
        },
      },
    ];
  }

  getPayrollData(date: Date): void {
    this.incomeService.getIncomes(moment(date).format('YYYY')).subscribe((res: any) => {
      if (res.content) {
        this.employeeIncome = res.content;
      } else {
        this.employeeIncome = res;
      }
      if (this.employeeIncome?.length) {
        this.calculateSum();
        this.msgs = [];
      } else {
        this.msgs = [{ severity: 'info', detail: this.translate.instant('entity.table.noData') }];
      }
    });

    this.getExistPdfInformation();
  }

  calculateSum(): void {
    this.total.top.sum = [];
    this.total.bottom.sum = [];
    this.employeeIncome.forEach(employeeIncome => {
      this.total.top.fields.forEach((field: string, index: number) => {
        if (!this.total.top.sum[index]) {
          this.total.top.sum[index] = 0;
        }
        this.total.top.sum[index] += get(employeeIncome, field) || 0;

        if (field === 'incomeFromBeginningOfYear') {
          this.total.top.sum[index] = get(employeeIncome, field);
        }
      });

      this.total.bottom.fields.forEach((field: string, index: number) => {
        if (!this.total.bottom.sum[index]) {
          this.total.bottom.sum[index] = 0;
        }
        this.total.bottom.sum[index] += get(employeeIncome, field) || 0;

        if (field === 'rate') {
          this.total.bottom.sum[index] = null;
        }
      });
    });
  }

  handlerChangeYear(isSetCurrentYear = false): void {
    if (isSetCurrentYear) {
      this.currentYear = moment().toDate();
    } else {
      this.currentYear = moment(this.currentYear).subtract(1, 'year').toDate();
    }
    this.location.replaceState(`payroll/salaries/${this.currentYearAsNumber}`);
    this.getPayrollData(this.currentYear);
  }

  displayDropdownMenu(): void {
    this.dropdownMenuVisibility = !this.dropdownMenuVisibility;
  }

  displayDialog(): void {
    this.dialogVisibility = true;
  }

  showFullscreenTable(): void {
    this.fullScreenTable = true;
  }

  goToPayrollDetails(date: number): void {
    const timestamp = moment(date, 'x');
    const year = timestamp.format('YYYY');
    const month = timestamp.format('MM');
    this.router.navigate([`payroll/salaries/${this.currentYearAsNumber}/details`, year, month]);
  }
}
