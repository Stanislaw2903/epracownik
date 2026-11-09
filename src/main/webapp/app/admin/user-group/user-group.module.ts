import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';

import { RouterModule } from '@angular/router';
import { EbokSharedModule } from 'app/shared/shared.module';
import { UserGroupListComponent } from './user-group.component';
import { userGroupRoute } from './user-group.route';
import { AddGroupComponent } from './add-group/add-group.component';
import { EditGroupComponent } from './edit-group/edit-group.component';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';

@NgModule({
  declarations: [UserGroupListComponent, AddGroupComponent, EditGroupComponent],
  imports: [
    EbokSharedModule,
    RouterModule.forChild(userGroupRoute),
    HttpClientModule,
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    ConfirmDialogModule,
    ToastModule,
    TooltipModule,
  ],
})
export class UserGroupModule {}
