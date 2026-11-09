import { SummaryComponent } from './steps/summary/summary.component';
import { TermsComponent } from './steps/terms/terms.component';
import { SystemDataComponent } from './steps/system.component';
import { PersonalComponent } from './steps/personal.component';
import { Route } from '@angular/router';

import { RegisterComponent } from './register.component';

export const registerRoute: Route = {
  path: 'register',
  component: RegisterComponent,
  data: {
    authorities: [],
    pageTitle: 'register.title',
  },
  children: [
    {
      path: 'personal',
      component: PersonalComponent,
    },
    {
      path: 'system',
      component: SystemDataComponent,
    },
    {
      path: 'terms',
      component: TermsComponent,
    },
    {
      path: 'summary',
      component: SummaryComponent,
    },
  ],
};
