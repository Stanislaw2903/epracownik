import { DecimalPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IncomesService } from '../incomes.service';

interface IPayrollDate {
  year: number;
  month: number;
}
@Component({
  selector: 'jhi-payroll-details',
  templateUrl: './payroll-details.component.html',
  styleUrls: ['./payroll-details.component.scss'],
  providers: [DecimalPipe],
})
export class PayrollDetailsComponent implements OnInit {
  payrollDate!: IPayrollDate;

  deductionsDict: any;
  ingidientsDict: any;
  breaksDict: any;
  payrollDetails: any;
  tableDetails!: any[];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private incomesService: IncomesService,
    private _decimalPipe: DecimalPipe
  ) {
    this.route.params.subscribe(params => {
      if (!params['year'] || !params['month']) {
        this.router.navigate(['..'], { relativeTo: this.route });
      }
      this.payrollDate = {
        year: params['year'],
        month: params['month'],
      };
    });

    this.incomesService.getIngridients().subscribe(dict => {
      this.ingidientsDict = dict;
    });

    this.incomesService.getDeductions().subscribe(dict => {
      this.deductionsDict = dict;
    });

    this.incomesService.getBreaks().subscribe(dict => {
      this.breaksDict = dict;
    });
  }

  ngOnInit(): void {
    if (this.payrollDate.year && this.payrollDate.month) {
      this.incomesService.getAdditionalDescription(this.payrollDate.year, this.payrollDate.month).subscribe(payrollDetails => {
        this.payrollDetails = payrollDetails;
        this.tableDetails = [payrollDetails?.content];

        const newIngridients: any[] = [
          {
            code: null,
            price: 0,
            priceT: 0,
            price3: 0,
          },
        ];

        this.payrollDetails.ingredients.forEach((item: { code: number; price: number; type: string }) => {
          if (item.type === 'T' || item.type === '3') {
            newIngridients[0].price += item.price;
            if (item.type === 'T') {
              newIngridients[0].priceT += item.price;
            }

            if (item.type === '3') {
              newIngridients[0].price3 += item.price;
            }
          } else {
            newIngridients.push(item);
          }
        });

        const filterEmptyIngridients = newIngridients.filter(item => {
          if (item.code === null) {
            return item.price3 || item.priceT;
          }
          return true;
        });
        this.payrollDetails.ingredients = filterEmptyIngridients.reverse();
      });
    }
  }

  getIngridientsCode(ingridients: { code: number; price: number; type: string; priceT: number; price3: number }): string {
    if (!ingridients.priceT && !ingridients.price3) {
      return this.ingidientsDict.find((item: { code: number }) => item.code === ingridients.code)?.name ?? '';
    }
    const ingridientName = this.ingidientsDict.find((item: { code: number }) => item.code === ingridients.code)?.name ?? '';

    return `${ingridientName || 'Wyn. urlopowe'} (skł. stałe/zmienne: ${this._decimalPipe.transform(
      ingridients.priceT ?? 0,
      '1.2-2'
    )} / ${this._decimalPipe.transform(ingridients.price3 ?? 0, '1.2-2')})`;
  }

  getBreaksCode(code: number): string {
    return this.breaksDict.find((b: any) => b.code === code)?.name ?? '';
  }

  getDeductionsCode(code: number): string {
    return this.deductionsDict.find((deductions: { code: number }) => deductions.code === code)?.name ?? '';
  }

  sumIngridients(): number {
    const ingridientsSum = this.payrollDetails?.ingredients?.reduce((acc: any, ingridients: { price: any }) => acc + ingridients.price, 0);
    const breaksSum = this.payrollDetails?.breaks?.reduce((acc: any, ingridients: { price: any }) => acc + ingridients.price, 0);
    return ingridientsSum + breaksSum;
  }

  sumDeductions(): number {
    return this.payrollDetails?.deductions?.reduce((acc: any, deductions: { price: any }) => acc + deductions.price, 0);
  }

  goToMainTable(): void {
    this.router.navigate([`/payroll/salaries/${this.payrollDate.year}`]);
  }
}
