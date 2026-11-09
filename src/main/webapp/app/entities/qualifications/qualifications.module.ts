import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EbokSharedModule } from 'app/shared/shared.module';
import { QualificationsComponent } from './qualifications.component';
import { QualificationsDetailComponent } from './qualifications-detail.component';
import { QualificationsUpdateComponent } from './qualifications-update.component';
import { QualificationsDeleteDialogComponent } from './qualifications-delete-dialog.component';
import { qualificationsRoute } from './qualifications.route';

@NgModule({
  imports: [EbokSharedModule, RouterModule.forChild(qualificationsRoute)],
  declarations: [
    QualificationsComponent,
    QualificationsDetailComponent,
    QualificationsUpdateComponent,
    QualificationsDeleteDialogComponent,
  ],
  entryComponents: [QualificationsDeleteDialogComponent],
})
export class EbokQualificationsModule {}
