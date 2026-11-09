import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from 'app/core/user/user.service';
import { ToastService } from 'app/shared/toast/toast.service';
import { IIntegrationStatus } from './../../../shared/model/users-intergation-status.model';

@Component({
  selector: 'jhi-integrate-users',
  templateUrl: './integrate-users.component.html',
})
export class IntegrateUsersComponent implements OnInit {
  integrationStatus!: IIntegrationStatus;
  countOfIntegratedUsers!: number;

  constructor(private userService: UserService, public activeModal: NgbActiveModal, private _toastService: ToastService) {}

  ngOnInit(): void {
    this.userService.updateUserDatabase({ contractor: this.userService.integrateWithMandory }).subscribe(resp => {
      this.countOfIntegratedUsers = resp.success.length;
      this.integrationStatus = resp;

      this._toastService.sendMessage({
        type: 'success',
        title: 'userManagement.home.integrate',
        message: 'userManagement.operationSuccessful',
      });
    });
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
