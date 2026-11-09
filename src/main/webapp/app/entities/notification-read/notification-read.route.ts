import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { INotificationRead, NotificationRead } from 'app/shared/model/notification-read.model';
import { NotificationReadService } from './notification-read.service';
import { NotificationReadComponent } from './notification-read.component';
import { NotificationReadDetailComponent } from './notification-read-detail.component';
import { NotificationReadUpdateComponent } from './notification-read-update.component';

@Injectable({ providedIn: 'root' })
export class NotificationReadResolve implements Resolve<INotificationRead> {
  constructor(private service: NotificationReadService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INotificationRead> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((notificationRead: HttpResponse<NotificationRead>) => {
          if (notificationRead.body) {
            return of(notificationRead.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NotificationRead());
  }
}

export const notificationReadRoute: Routes = [
  {
    path: '',
    component: NotificationReadComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.notificationRead.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NotificationReadDetailComponent,
    resolve: {
      notificationRead: NotificationReadResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.notificationRead.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NotificationReadUpdateComponent,
    resolve: {
      notificationRead: NotificationReadResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.notificationRead.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NotificationReadUpdateComponent,
    resolve: {
      notificationRead: NotificationReadResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.notificationRead.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
