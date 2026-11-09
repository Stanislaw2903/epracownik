import { NgModule } from '@angular/core';
import { EbokSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { LoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { DateMomentPipe } from './pipes/date-moment.pipe';

@NgModule({
  imports: [EbokSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    DateMomentPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    EbokSharedLibsModule,
    FindLanguageFromKeyPipe,
    DateMomentPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
  ],
})
export class EbokSharedModule {}
