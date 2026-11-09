import { PaginatorModule } from 'primeng/paginator';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EbokSharedModule } from 'app/shared/shared.module';
import { UserManagementComponent } from './user-management.component';
import { UserManagementDetailComponent } from './user-management-detail.component';
import { UserManagementUpdateComponent } from './user-management-update.component';
import { UserManagementDeleteDialogComponent } from './user-management-delete-dialog.component';
import { userManagementRoute } from './user-management.route';
import { InputSwitchModule } from 'primeng/inputswitch';
import { MultiSelectModule } from 'primeng/multiselect';
import { DropdownModule } from 'primeng/dropdown';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { IntegrateUsersComponent } from './integrate-users/integrate-users.component';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ResendActivationComponent } from './resend-activation/resend-activation.component';
import { InputMaskModule } from 'primeng/inputmask';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';

@NgModule({
  imports: [
    EbokSharedModule,
    RouterModule.forChild(userManagementRoute),
    InputSwitchModule,
    MultiSelectModule,
    ButtonModule,
    TooltipModule,
    DropdownModule,
    ScrollPanelModule,
    ConfirmDialogModule,
    ToggleButtonModule,
    PaginatorModule,
    TableModule,
    InputMaskModule,
    DialogModule,
    CheckboxModule,
  ],
  declarations: [
    UserManagementComponent,
    UserManagementDetailComponent,
    UserManagementUpdateComponent,
    UserManagementDeleteDialogComponent,
    IntegrateUsersComponent,
    ResendActivationComponent,
  ],
  entryComponents: [UserManagementDeleteDialogComponent],
})
export class UserManagementModule {}
