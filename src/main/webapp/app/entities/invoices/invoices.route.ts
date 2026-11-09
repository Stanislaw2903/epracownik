import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IInvoices, Invoices } from 'app/shared/model/invoices.model';
import { InvoicesService } from './invoices.service';
import { InvoicesComponent } from './invoices.component';
import { InvoicesDetailComponent } from './invoices-detail.component';
import { InvoicesUpdateComponent } from './invoices-update.component';

@Injectable({ providedIn: 'root' })
export class InvoicesResolve implements Resolve<IInvoices> {
  constructor(private service: InvoicesService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IInvoices> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((invoices: HttpResponse<Invoices>) => {
          if (invoices.body) {
            return of(invoices.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Invoices());
  }
}

export const invoicesRoute: Routes = [
  {
    path: '',
    component: InvoicesComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'ebokApp.invoices.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InvoicesDetailComponent,
    resolve: {
      invoices: InvoicesResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.invoices.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InvoicesUpdateComponent,
    resolve: {
      invoices: InvoicesResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.invoices.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InvoicesUpdateComponent,
    resolve: {
      invoices: InvoicesResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'ebokApp.invoices.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
