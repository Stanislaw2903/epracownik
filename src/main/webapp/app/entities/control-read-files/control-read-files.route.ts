import { Injectable } from '@angular/core';
import { Resolve, Router, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Authority } from 'app/shared/constants/authority.constants';
import { Observable, of } from 'rxjs';
import { ControlReadFiles, IControlReadFiles } from '../../shared/model/control-read-files';
import { ControlReadFilesComponent } from './control-read-files.component';

@Injectable({ providedIn: 'root' })
export class ControlReadFilesResolve implements Resolve<IControlReadFiles> {
  constructor(private router: Router) {}

  resolve(): Observable<IControlReadFiles> | Observable<never> {
    return of(new ControlReadFiles());
  }
}

export const controlReadFilesRoute: Routes = [
  {
    path: '',
    component: ControlReadFilesComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'viewed,desc',
      pageTitle: 'ebokApp.controlReadFiles.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
