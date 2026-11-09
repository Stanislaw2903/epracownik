import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { EbokSharedModule } from 'app/shared/shared.module';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { EditorModule } from 'primeng/editor';
import { FieldsetModule } from 'primeng/fieldset';
import { MultiSelectModule } from 'primeng/multiselect';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TableModule } from 'primeng/table';
import { ToolbarModule } from 'primeng/toolbar';
import { TooltipModule } from 'primeng/tooltip';
import { NotificationDeleteDialogComponent } from './notification-delete-dialog.component';
import { NotificationDetailComponent } from './notification-detail.component';
import { NotificationUpdateComponent } from './notification-update.component';
import { NotificationComponent } from './notification.component';
import { notificationRoute } from './notification.route';
@NgModule({
  imports: [
    EbokSharedModule,
    RouterModule.forChild(notificationRoute),
    TableModule,
    ButtonModule,
    EditorModule,
    CardModule,
    DropdownModule,
    MultiSelectModule,
    OverlayPanelModule,
    TooltipModule,
    FieldsetModule,
    ToolbarModule,
    TranslateModule,
    RadioButtonModule,
  ],
  declarations: [NotificationComponent, NotificationDetailComponent, NotificationUpdateComponent, NotificationDeleteDialogComponent],
  entryComponents: [NotificationDeleteDialogComponent],
})
export class EbokNotificationModule {}
