import { GraphicService } from './graphic.service';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Authority } from 'app/shared/constants/authority.constants';
import { GraphicComponent } from './graphic.component';
import { Resolve, Router, Routes } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { IGraphicTableItem, Graphic } from '../../shared/model/graphic.model';

@Injectable({ providedIn: 'root' })
export class GraphicResolve implements Resolve<IGraphicTableItem> {
  constructor(private service: GraphicService, private router: Router) {}

  resolve(): Observable<IGraphicTableItem> | Observable<never> {
    return of(new Graphic());
  }
}

export const graphicRoute: Routes = [
  {
    path: '',
    component: GraphicComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'ebokApp.vacations.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
