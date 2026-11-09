import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserService } from 'app/core/user/user.service';
import { ToastService } from 'app/shared/toast/toast.service';
import { IIntegrationCandidate, IIntegrationStatus } from './../../../shared/model/users-intergation-status.model';

@Component({
  selector: 'jhi-integrate-users',
  templateUrl: './integrate-users.component.html',
})
export class IntegrateUsersComponent implements OnInit {
  candidates: IIntegrationCandidate[] = [];
  selected: IIntegrationCandidate[] = [];
  contractor = false;
  loading = false;
  importing = false;
  integrationStatus?: IIntegrationStatus;
  countOfIntegratedUsers?: number;

  constructor(private userService: UserService, public activeModal: NgbActiveModal, private _toastService: ToastService) {}

  ngOnInit(): void {
    this.loadCandidates();
  }

  loadCandidates(): void {
    this.loading = true;
    this.userService.getIntegrationCandidates(this.contractor).subscribe(
      candidates => {
        this.candidates = candidates || [];
        this.selectAll();
        this.loading = false;
      },
      () => (this.loading = false)
    );
  }

  importable(): IIntegrationCandidate[] {
    return this.candidates.filter(candidate => !candidate.problem);
  }

  selectAll(): void {
    this.selected = this.importable();
  }

  deselectAll(): void {
    this.selected = [];
  }

  import(): void {
    if (!this.selected.length || this.importing) {
      return;
    }
    this.importing = true;
    this.userService
      .updateUserDatabase({ contractor: this.contractor, externalIds: this.selected.map(candidate => candidate.externalId) })
      .subscribe(
        resp => {
          this.importing = false;
          this.countOfIntegratedUsers = resp.success.length;
          this.integrationStatus = resp;
          this._toastService.sendMessage({
            type: 'success',
            title: 'userManagement.home.integrate',
            message: 'userManagement.operationSuccessful',
          });
        },
        () => (this.importing = false)
      );
  }

  close(): void {
    if (this.integrationStatus) {
      this.activeModal.close(true);
    } else {
      this.activeModal.dismiss();
    }
  }
}
