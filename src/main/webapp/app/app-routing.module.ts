import { LoggedComponent } from './layouts/logged/logged.component';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { Authority } from 'app/shared/constants/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { VisitorComponent } from './layouts/visitor/visitor.component';
import { LoginPageComponent } from './login/login-page.component';
import { PasswordComponent } from './account/password/password.component';
import { SettingsComponent } from './account/settings/settings.component';

const LAYOUT_ROUTES = [...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: '',
          component: LoggedComponent,
          canActivate: [UserRouteAccessService],
          data: {
            authorities: [Authority.ADMIN, Authority.USER],
          },
          children: [
            {
              path: 'admin',
              data: {
                authorities: [Authority.ADMIN],
              },
              canActivate: [UserRouteAccessService],
              loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
            },
            {
              path: '',
              data: {
                path: 'entity',
              },
              loadChildren: () => import('./entities/entity.module').then(m => m.EbokEntityModule),
            },
            {
              path: 'dashboard',
              data: {
                cardException: true,
              },
              loadChildren: () => import('./home/home.module').then(m => m.EbokHomeModule),
            },
            {
              path: 'password',
              component: PasswordComponent,
              data: {
                authorities: [Authority.USER],
                pageTitle: 'global.menu.account.password',
              },
              canActivate: [UserRouteAccessService],
            },
            {
              path: 'settings',
              component: SettingsComponent,
              data: {
                authorities: [Authority.USER],
                pageTitle: 'global.menu.account.settings',
              },
              canActivate: [UserRouteAccessService],
            },
          ],
        },
        {
          path: '',
          component: VisitorComponent,
          children: [
            {
              path: 'login',
              component: LoginPageComponent,
              data: {
                pageTitle: 'login.title',
              },
            },
            {
              path: 'account',
              loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
            },
          ],
        },
        ...LAYOUT_ROUTES,
      ],
      {
        enableTracing: false, // TODO: change to: DEBUG_INFO_ENABLED
      }
    ),
  ],
  exports: [RouterModule],
})
export class EbokAppRoutingModule {}
