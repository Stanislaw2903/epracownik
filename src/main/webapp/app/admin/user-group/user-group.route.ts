import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';

import { UserGroupListComponent } from './user-group.component';
import { AddGroupComponent } from './add-group/add-group.component';
import { EditGroupComponent } from './edit-group/edit-group.component';
import { Injectable } from '@angular/core';
import { IUser, User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { Observable, of } from 'rxjs';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Authority } from 'app/shared/constants/authority.constants';

@Injectable({ providedIn: 'root' })
export class UserManagementResolve implements Resolve<IUser> {
  constructor(private service: UserService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUser> {
    const id = route.params['login'];
    if (id) {
      return this.service.find(id);
    }
    return of(new User());
  }
}

export const userGroupRoute: Routes = [
  {
    path: '',
    component: UserGroupListComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'userGroups.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'add',
    component: AddGroupComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'userGroups.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'edit',
    component: EditGroupComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'userGroups.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
