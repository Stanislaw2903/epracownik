import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Authority } from 'app/shared/constants/authority.constants';
import { Injectable } from '@angular/core';
import { Resolve, Routes, Router } from '@angular/router';
import { Observable, of } from 'rxjs';

import { IVacationsModel, Vacations } from '../../shared/model/vacations.model';
import { VacationsService } from './vacations.service';
import { VacationsComponent } from './vacations.component';

@Injectable({ providedIn: 'root' })
export class VacationsResolve implements Resolve<IVacationsModel> {
  constructor(private service: VacationsService, private router: Router) {}

  resolve(): Observable<IVacationsModel> | Observable<never> {
    return of(new Vacations());
  }
}

export const vacationRoute: Routes = [
  {
    path: '',
    component: VacationsComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'ebokApp.vacations.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
