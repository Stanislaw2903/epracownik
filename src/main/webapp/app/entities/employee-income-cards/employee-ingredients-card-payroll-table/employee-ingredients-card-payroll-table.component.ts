import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as moment from 'moment';
import { Message, MessageService } from 'primeng/api';
import { Subject } from 'rxjs';
import { IncomesService } from '../incomes.service';

@Component({
  selector: 'jhi-employee-ingredients-card-payroll-table',
  templateUrl: './employee-ingredients-card-payroll-table.component.html',
  styleUrls: ['./employee-ingredients-card-payroll-table.component.scss'],
  providers: [MessageService],
})
export class EmployeeIngredientsCardPayrollTableComponent implements OnInit, OnChanges {
  @Input() currentSelectedYear!: Date;
  @Input() isOpen!: boolean;

  private isOpen$: Subject<boolean>;

  employeeIncome: any = {};
  sums: number[] = [];

  msgs: Message[] = [];

  constructor(private incomesService: IncomesService, private translate: TranslateService) {
    this.isOpen$ = new Subject<boolean>();
  }

  ngOnInit(): void {
    this.isOpen$.subscribe((isOpened: boolean) => {
      if (isOpened) {
        this.employeeIncome.employeeIncomeStartDate = moment(this.currentSelectedYear).startOf('year').toDate();
        this.employeeIncome.employeeIncomeEndDate = moment(this.currentSelectedYear).endOf('year').toDate();

        this.incomesService.getIncomesDetailsByListId(moment(this.currentSelectedYear).format('YYYY')).subscribe(data => {
          if (data.content) {
            this.employeeIncome.employeeIncomeData = data.content;
          } else {
            this.employeeIncome.employeeIncomeData = data;
          }

          if (this.employeeIncome.employeeIncomeData.rows.length) {
            this.sums = new Array(this.employeeIncome.employeeIncomeData.columns.length).fill(
              0,
              0,
              this.employeeIncome.employeeIncomeData.columns.length
            );
            this.employeeIncome.employeeIncomeData.data = [];
            this.employeeIncome.employeeIncomeData.rows.forEach((row: any) => {
              const tableRowIndex = this.employeeIncome.employeeIncomeData.data.findIndex(
                (dataRow: any) => dataRow.paymentDate === row.paymentDate
              );
              if (tableRowIndex === -1) {
                const rowData: any = {
                  paymentDate: row.paymentDate,
                  rowAmount: new Array(this.employeeIncome.employeeIncomeData.columns.length).fill(
                    0,
                    0,
                    this.employeeIncome.employeeIncomeData.columns.length
                  ),
                };

                const columnIdx = this.employeeIncome.employeeIncomeData.columns.findIndex(
                  (column: any) => column.code.toString() === row.code.toString()
                );
                rowData.rowAmount[columnIdx] = row.amount;
                this.sums[columnIdx] += row.amount;
                this.employeeIncome.employeeIncomeData.data.push(rowData);
              } else {
                const rowData: any = this.employeeIncome.employeeIncomeData.data[tableRowIndex];

                const columnIdx = this.employeeIncome.employeeIncomeData.columns.findIndex(
                  (column: any) => column.code.toString() === row.code.toString()
                );
                rowData.rowAmount[columnIdx] = row.amount;
                this.sums[columnIdx] += row.amount;
              }
            });
            this.msgs = [];
          } else {
            this.msgs = [{ severity: 'info', detail: this.translate.instant('entity.table.noData') }];
          }
        });
      }
    });
  }

  ngOnChanges(): void {
    this.isOpen$.next(this.isOpen);
  }
}
