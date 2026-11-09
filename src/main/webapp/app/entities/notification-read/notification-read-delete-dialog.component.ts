import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { INotificationRead } from 'app/shared/model/notification-read.model';
import { NotificationReadService } from './notification-read.service';

@Component({
  templateUrl: './notification-read-delete-dialog.component.html',
})
export class NotificationReadDeleteDialogComponent {
  notificationRead?: INotificationRead;

  constructor(
    protected notificationReadService: NotificationReadService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.notificationReadService.delete(id).subscribe(() => {
      this.eventManager.broadcast('notificationReadListModification');
      this.activeModal.close();
    });
  }
}
