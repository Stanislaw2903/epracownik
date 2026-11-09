import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INotificationRead } from 'app/shared/model/notification-read.model';
import { NotificationReadService } from './notification-read.service';
import { NotificationReadDeleteDialogComponent } from './notification-read-delete-dialog.component';

@Component({
  selector: 'jhi-notification-read',
  templateUrl: './notification-read.component.html',
})
export class NotificationReadComponent implements OnInit, OnDestroy {
  notificationReads?: INotificationRead[];
  eventSubscriber?: Subscription;

  constructor(
    protected notificationReadService: NotificationReadService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.notificationReadService.query().subscribe((res: HttpResponse<INotificationRead[]>) => (this.notificationReads = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInNotificationReads();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: INotificationRead): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInNotificationReads(): void {
    this.eventSubscriber = this.eventManager.subscribe('notificationReadListModification', () => this.loadAll());
  }

  delete(notificationRead: INotificationRead): void {
    const modalRef = this.modalService.open(NotificationReadDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.notificationRead = notificationRead;
  }
}
