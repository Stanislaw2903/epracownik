import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EbokSharedModule } from 'app/shared/shared.module';
import { InputSwitchModule } from 'primeng/inputswitch';

import { ConfigurationComponent } from './configuration.component';

import { configurationRoute } from './configuration.route';

@NgModule({
  imports: [EbokSharedModule, RouterModule.forChild([configurationRoute]), InputSwitchModule, FormsModule],
  declarations: [ConfigurationComponent],
})
export class ConfigurationModule {}
