import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EbokSharedModule } from 'app/shared/shared.module';

import { PasswordStrengthBarComponent } from './password/password-strength-bar.component';
import { RegisterComponent } from './register/register.component';
import { ActivateComponent } from './activate/activate.component';
import { PasswordComponent } from './password/password.component';
import { PasswordResetInitComponent } from './password-reset/init/password-reset-init.component';
import { PasswordResetFinishComponent } from './password-reset/finish/password-reset-finish.component';
import { SettingsComponent } from './settings/settings.component';
import { accountState } from './account.route';
import { StepsModule } from 'primeng/steps';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputMaskModule } from 'primeng/inputmask';
import { PanelModule } from 'primeng/panel';
import { CheckboxModule } from 'primeng/checkbox';
import { PersonalComponent } from './register/steps/personal.component';
import { ButtonModule } from 'primeng/button';
import { SelectButtonModule } from 'primeng/selectbutton';
import { SystemDataComponent } from './register/steps/system.component';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
import { TermsComponent } from './register/steps/terms/terms.component';
import { SummaryComponent } from './register/steps/summary/summary.component';

@NgModule({
  imports: [
    EbokSharedModule,
    StepsModule,
    CardModule,
    ButtonModule,
    InputTextModule,
    DropdownModule,
    InputMaskModule,
    CheckboxModule,
    SelectButtonModule,
    PanelModule,
    MessagesModule,
    MessageModule,
    RouterModule.forChild(accountState),
  ],
  declarations: [
    ActivateComponent,
    RegisterComponent,
    PersonalComponent,
    SystemDataComponent,
    PasswordComponent,
    PasswordStrengthBarComponent,
    PasswordResetInitComponent,
    PasswordResetFinishComponent,
    SettingsComponent,
    TermsComponent,
    SummaryComponent,
  ],
})
export class AccountModule {}
