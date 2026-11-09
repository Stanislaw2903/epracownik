import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INotification } from 'app/shared/model/notification.model';

@Component({
  selector: 'jhi-notification-detail',
  templateUrl: './notification-detail.component.html',
})
export class NotificationDetailComponent implements OnInit {
  notification: INotification | null = null;
  recipients: any[] = [];

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => (this.notification = notification));
    this.recipients = this.notification?.recepients as any;
  }

  previousState(): void {
    window.history.back();
  }
}
