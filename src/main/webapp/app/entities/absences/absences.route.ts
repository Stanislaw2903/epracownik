import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Authority } from 'app/shared/constants/authority.constants';
import { Absences, IAbsences } from './../../shared/model/absences.model';
import { Injectable } from '@angular/core';
import { Resolve, Routes, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AbsencesService } from './absences.service';
import { AbsencesComponent } from './absences.component';

@Injectable({ providedIn: 'root' })
export class AbsencesResolve implements Resolve<IAbsences> {
  constructor(private service: AbsencesService, private router: Router) {}

  resolve(): Observable<IAbsences> | Observable<never> {
    return of(new Absences());
  }
}

export const absencesRoute: Routes = [
  {
    path: '',
    component: AbsencesComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'dateFrom,desc',
      pageTitle: 'ebokApp.absences.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
