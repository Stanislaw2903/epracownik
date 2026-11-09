import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'invoices',
        loadChildren: () => import('./invoices/invoices.module').then(m => m.EbokInvoicesModule),
      },
      {
        path: 'payroll',
        data: {
          pageTitle: 'global.menu.entities.payroll',
        },
        loadChildren: () => import('./employee-income-cards/employee-income-cards.module').then(m => m.EmployeeIncomeCardsModule),
      },
      {
        path: 'notification',
        data: {
          pageTitle: 'global.menu.administration',
        },
        loadChildren: () => import('./notification/notification.module').then(m => m.EbokNotificationModule),
      },
      {
        path: 'notification-read',
        loadChildren: () => import('./notification-read/notification-read.module').then(m => m.EbokNotificationReadModule),
      },
      {
        path: 'personal-data',
        data: {
          pageTitle: 'global.menu.entities.personnelData',
        },
        loadChildren: () => import('./personal/personal.module').then(m => m.PersonalModule),
      },
      {
        path: 'qualifications',
        loadChildren: () => import('./qualifications/qualifications.module').then(m => m.EbokQualificationsModule),
      },
      {
        path: 'vacations',
        data: {
          pageTitle: 'global.menu.administration',
        },
        loadChildren: () => import('./vacations/vacations.module').then(m => m.VacationsModule),
      },
      {
        path: 'absences',
        loadChildren: () => import('./absences/absences.module').then(m => m.AbsencesModule),
      },
      {
        path: 'control-read-files',
        data: {
          pageTitle: 'global.menu.administration',
        },
        loadChildren: () => import('./control-read-files/control-read-files.module').then(m => m.ControlReadFilesModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EbokEntityModule {}
