import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INotificationRead } from 'app/shared/model/notification-read.model';

@Component({
  selector: 'jhi-notification-read-detail',
  templateUrl: './notification-read-detail.component.html',
})
export class NotificationReadDetailComponent implements OnInit {
  notificationRead: INotificationRead | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notificationRead }) => (this.notificationRead = notificationRead));
  }

  previousState(): void {
    window.history.back();
  }
}
