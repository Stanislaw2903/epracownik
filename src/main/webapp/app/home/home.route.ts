import { Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Authority } from 'app/shared/constants/authority.constants';

import { HomeComponent } from './home.component';
import { UserVacationComponent } from './user-vacation/user-vacation.component';

export const HOME_ROUTE: Routes = [
  {
    path: '',
    component: HomeComponent,
    data: {
      authorities: [Authority.ADMIN, Authority.USER],
      pageTitle: 'home.title',
      cardException: true,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'user-vacation',
    component: UserVacationComponent,
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'ebokApp.vacations.home.ownVacations',
    },
  },
];
