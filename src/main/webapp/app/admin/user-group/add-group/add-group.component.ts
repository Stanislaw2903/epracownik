import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserGroupService } from '../user-group.service';
import { MessageService } from 'primeng/api';
import { ToastService } from 'app/shared/toast/toast.service';

@Component({
  selector: 'jhi-add-group',
  templateUrl: './add-group.component.html',
  providers: [MessageService],
})
export class AddGroupComponent {
  group = {
    code: '',
    name: '',
  };

  constructor(
    private userGroupService: UserGroupService,
    private router: Router,
    private route: ActivatedRoute,
    private _toastService: ToastService
  ) {}

  saveGroup(): void {
    this.userGroupService.createUserGroup(this.group).subscribe(() => {
      this._toastService.sendMessage({
        type: 'success',
        title: 'info',
        message: 'userGroups.messages.created',
      });
      this.router.navigate(['..'], { relativeTo: this.route });
    });
  }

  previousState(): void {
    window.history.back();
  }
}
