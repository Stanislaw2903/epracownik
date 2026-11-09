import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { INotificationRead, NotificationRead } from 'app/shared/model/notification-read.model';
import { NotificationReadService } from './notification-read.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-notification-read-update',
  templateUrl: './notification-read-update.component.html',
})
export class NotificationReadUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    date: [],
    notify: [],
    users: [],
  });

  constructor(
    protected notificationReadService: NotificationReadService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notificationRead }) => {
      if (!notificationRead.id) {
        const today = moment().startOf('day');
        notificationRead.date = today;
      }

      this.updateForm(notificationRead);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(notificationRead: INotificationRead): void {
    this.editForm.patchValue({
      id: notificationRead.id,
      date: notificationRead.date ? notificationRead.date.format(DATE_TIME_FORMAT) : null,
      notify: notificationRead.notify,
      users: notificationRead.users,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notificationRead = this.createFromForm();
    if (notificationRead.id !== undefined) {
      this.subscribeToSaveResponse(this.notificationReadService.update(notificationRead));
    } else {
      this.subscribeToSaveResponse(this.notificationReadService.create(notificationRead));
    }
  }

  private createFromForm(): INotificationRead {
    return {
      ...new NotificationRead(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      notify: this.editForm.get(['notify'])!.value,
      users: this.editForm.get(['users'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotificationRead>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IUser): any {
    return item.id;
  }

  getSelected(selectedVals: IUser[], option: IUser): IUser {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
