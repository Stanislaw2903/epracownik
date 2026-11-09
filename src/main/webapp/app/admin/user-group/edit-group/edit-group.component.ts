import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserGroupService } from '../user-group.service';
import { UserGroupStateService } from '../user-group-state.service';
import { ToastService } from 'app/shared/toast/toast.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'jhi-edit-group',
  templateUrl: './edit-group.component.html',
  providers: [MessageService],
})
export class EditGroupComponent implements OnInit {
  group: any = {
    code: '',
    name: '',
  };

  constructor(
    private router: Router,
    private userGroupService: UserGroupService,
    private userGroupStateService: UserGroupStateService,
    private _toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.group = this.userGroupStateService.getGroup();

    if (!this.group) {
      // Brak danych - przekieruj na ekran tabeli
      this.router.navigate(['admin/user-groups']);
    }
  }

  updateGroup(): void {
    this.userGroupService.updateUserGroup(this.group).subscribe(() => {
      this._toastService.sendMessage({
        type: 'success',
        title: 'info',
        message: 'userGroups.messages.updated',
      });
      this.router.navigate(['admin/user-groups']);
    });
  }

  previousState(): void {
    window.history.back();
  }
}
