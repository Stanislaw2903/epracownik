import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { EbokSharedModule } from 'app/shared/shared.module';
import { EmployeeDataGridTableComponent } from './employee-data-grid-table/employee-data-grid-table.component';
import { EmployeeIncomeCardPayrollTableComponent } from './employee-income-card-payroll-table/employee-income-card-payroll-table.component';
import { EmployeeIncomeCardsComponent } from './employee-income-cards.component';
import { EmployeeIngredientsCardPayrollTableComponent } from './employee-ingredients-card-payroll-table/employee-ingredients-card-payroll-table.component';
import { IncomesService } from './incomes.service';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { employeeIncomeCardsRoutes } from './employee-income-cards.route';
import { MenuModule } from 'primeng/menu';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { MessagesModule } from 'primeng/messages';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { TooltipModule } from 'primeng/tooltip';
import { PayrollDetailsComponent } from './payroll-details/payroll-details.component';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';

@NgModule({
  declarations: [
    EmployeeIncomeCardsComponent,
    EmployeeIncomeCardPayrollTableComponent,
    EmployeeIngredientsCardPayrollTableComponent,
    EmployeeDataGridTableComponent,
    PayrollDetailsComponent,
  ],
  imports: [
    DialogModule,
    EbokSharedModule,
    TableModule,
    MenuModule,
    ToolbarModule,
    ButtonModule,
    TooltipModule,
    MessagesModule,
    ScrollPanelModule,
    CommonModule,
    NgxExtendedPdfViewerModule,
    RouterModule.forChild(employeeIncomeCardsRoutes),
  ],
  providers: [IncomesService],
})
export class EmployeeIncomeCardsModule {}
