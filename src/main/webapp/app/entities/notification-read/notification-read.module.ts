import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EbokSharedModule } from 'app/shared/shared.module';
import { NotificationReadComponent } from './notification-read.component';
import { NotificationReadDetailComponent } from './notification-read-detail.component';
import { NotificationReadUpdateComponent } from './notification-read-update.component';
import { NotificationReadDeleteDialogComponent } from './notification-read-delete-dialog.component';
import { notificationReadRoute } from './notification-read.route';

@NgModule({
  imports: [EbokSharedModule, RouterModule.forChild(notificationReadRoute)],
  declarations: [
    NotificationReadComponent,
    NotificationReadDetailComponent,
    NotificationReadUpdateComponent,
    NotificationReadDeleteDialogComponent,
  ],
  entryComponents: [NotificationReadDeleteDialogComponent],
})
export class EbokNotificationReadModule {}
