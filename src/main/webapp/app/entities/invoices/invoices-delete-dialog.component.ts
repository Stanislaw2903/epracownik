import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IInvoices } from 'app/shared/model/invoices.model';
import { InvoicesService } from './invoices.service';

@Component({
  templateUrl: './invoices-delete-dialog.component.html',
})
export class InvoicesDeleteDialogComponent {
  invoices?: IInvoices;

  constructor(protected invoicesService: InvoicesService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.invoicesService.delete(id).subscribe(() => {
      this.eventManager.broadcast('invoicesListModification');
      this.activeModal.close();
    });
  }
}
