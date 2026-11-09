import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IQualifications, Qualifications } from 'app/shared/model/qualifications.model';
import { QualificationsService } from './qualifications.service';
import { QualificationsComponent } from './qualifications.component';
import { QualificationsDetailComponent } from './qualifications-detail.component';
import { QualificationsUpdateComponent } from './qualifications-update.component';

@Injectable({ providedIn: 'root' })
export class QualificationsResolve implements Resolve<IQualifications> {
  constructor(private service: QualificationsService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IQualifications> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((qualifications: HttpResponse<Qualifications>) => {
          if (qualifications.body) {
            return of(qualifications.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Qualifications());
  }
}

export const qualificationsRoute: Routes = [
  {
    path: '',
    component: QualificationsComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'ebokApp.qualifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QualificationsDetailComponent,
    resolve: {
      qualifications: QualificationsResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.qualifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QualificationsUpdateComponent,
    resolve: {
      qualifications: QualificationsResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.qualifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QualificationsUpdateComponent,
    resolve: {
      qualifications: QualificationsResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.qualifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
