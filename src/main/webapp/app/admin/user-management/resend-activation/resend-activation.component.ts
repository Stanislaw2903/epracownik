import { IIntegrationStatus } from '../../../shared/model/users-intergation-status.model';
import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from 'app/core/user/user.service';
import { ToastService } from 'app/shared/toast/toast.service';

@Component({
  selector: 'jhi-resend-activation',
  templateUrl: './resend-activation.component.html',
})
export class ResendActivationComponent implements OnInit {
  integrationStatus!: IIntegrationStatus;
  countOfSendedUsers!: number;

  constructor(private userService: UserService, public activeModal: NgbActiveModal, private _toastService: ToastService) {}

  ngOnInit(): void {
    this.userService.resendMail().subscribe(resp => {
      this.countOfSendedUsers = resp.success.length;
      this.integrationStatus = resp;

      this._toastService.sendMessage({
        type: 'success',
        title: 'userManagement.home.emailActivation',
        message: 'userManagement.operationSuccessful',
      });
    });
  }

  cancel(): void {
    this.activeModal.dismiss();
  }
}
