import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IQualifications } from 'app/shared/model/qualifications.model';
import { QualificationsService } from './qualifications.service';

@Component({
  templateUrl: './qualifications-delete-dialog.component.html',
})
export class QualificationsDeleteDialogComponent {
  qualifications?: IQualifications;

  constructor(
    protected qualificationsService: QualificationsService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.qualificationsService.delete(id).subscribe(() => {
      this.eventManager.broadcast('qualificationsListModification');
      this.activeModal.close();
    });
  }
}
