import { Authority } from '../../shared/constants/authority.constants';
import { EmployeeIncomeCardsComponent } from './employee-income-cards.component';
import { Routes } from '@angular/router';
import { UserRouteAccessService } from '../../core/auth/user-route-access-service';
import { PayrollDetailsComponent } from './payroll-details/payroll-details.component';

export const employeeIncomeCardsRoutes: Routes = [
  {
    path: 'salaries',
    component: EmployeeIncomeCardsComponent,
    data: {
      pageTitle: 'global.menu.entities.salaries',
      authorities: [Authority.USER],
    },
    canActivate: [UserRouteAccessService],
    children: [],
  },
  {
    path: 'salaries/:payrollYear',
    component: EmployeeIncomeCardsComponent,
    data: {
      pageTitle: 'global.menu.entities.salaries',
      authorities: [Authority.USER],
    },
    canActivate: [UserRouteAccessService],
    children: [],
  },
  {
    path: 'salaries/:payrollYear/details/:year/:month',
    component: PayrollDetailsComponent,
    data: {
      pageTitle: 'global.menu.entities.payrollDetails',
      authorities: [Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'salaries/:payrollYear/details/:year',
    component: PayrollDetailsComponent,
    data: {
      pageTitle: 'global.menu.entities.payrollDetails',
      authorities: [Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'salaries/details',
    component: PayrollDetailsComponent,
    data: {
      pageTitle: 'global.menu.entities.payrollDetails',
      authorities: [Authority.USER],
    },
    canActivate: [UserRouteAccessService],
  },
];
