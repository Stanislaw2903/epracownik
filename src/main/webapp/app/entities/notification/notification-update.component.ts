import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import * as moment from 'moment';
import { Observable } from 'rxjs';

import { IUser, IUserGroup } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { notificationCategory } from 'app/shared/model/enumerations/notification-category.model';
import { notificationCanal } from 'app/shared/model/enumerations/notification-canal.model';
import { INotification, Notification } from 'app/shared/model/notification.model';
import { NotificationService } from './notification.service';

enum ENotificationType {
  NOTIFICATION = 'notification',
  SMS = 'sms',
  MAIL = 'mail',
}

enum ENotificationCanal {
  ALL = 'ALL',
  USERS = 'USERS',
  GROUPS = 'GROUPS',
}
@Component({
  selector: 'jhi-notification-update',
  templateUrl: './notification-update.component.html',
})
export class NotificationUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  groups: IUserGroup[] = [];
  description: any;
  statuses: any;

  selectedNotificationType!: ENotificationType;
  selectedNotificationCanal!: ENotificationCanal;
  multiselectPlaceholder = '';

  get ENotificationType(): typeof ENotificationType {
    return ENotificationType;
  }

  get ENotificationCanal(): typeof ENotificationCanal {
    return ENotificationCanal;
  }

  editForm = this.fb.group({
    id: [],
    subject: ['', [Validators.required]],
    description: ['', [Validators.required]],
    createDate: [],
    readDate: [],
    status: [],
    category: [],
    canal: [],
    createdBy: [],
    recepients: [''],
    groups: [''],
    sendNotification: true,
    sendSms: false,
    sendMail: false,
  });

  constructor(
    protected notificationService: NotificationService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      if (!notification.id) {
        const today = moment().startOf('day');
        notification.createDate = today;
        notification.readDate = today;
        notification.sendNotification = true;
      }

      this.updateForm(notification);

      this.getUsersDict(true);
    });

    this.statuses = [
      { label: 'MESSAGE', value: 'MESSAGE' },
      { label: 'SYSTEM', value: 'SYSTEM' },
      { label: 'ABSENT', value: 'ABSENT' },
      { label: 'HR', value: 'HR' },
    ];
  }

  updateForm(notification: INotification): void {
    if (notification.canal === null || notification.canal === undefined) {
      notification.canal = notificationCanal.ALL;
    }
    if (notification.canal === notificationCanal.ALL) {
      this.selectedNotificationCanal = ENotificationCanal.ALL;
    } else if (notification.canal === notificationCanal.USERS) {
      this.selectedNotificationCanal = ENotificationCanal.USERS;
    } else if (notification.canal === notificationCanal.GROUPS) {
      this.selectedNotificationCanal = ENotificationCanal.GROUPS;
    }

    if (notification.sendSms) {
      this.selectedNotificationType = ENotificationType.SMS;
    }
    if (notification.sendNotification) {
      this.selectedNotificationType = ENotificationType.NOTIFICATION;
    }
    if (notification.sendMail) {
      this.selectedNotificationType = ENotificationType.MAIL;
    }
    this.editForm.patchValue({
      id: notification.id,
      subject: notification.subject,
      description: notification.description,
      createDate: notification.createDate ? notification.createDate.format(DATE_TIME_FORMAT) : null,
      readDate: notification.readDate ? notification.readDate.format(DATE_TIME_FORMAT) : null,
      status: notification.status,
      category: 'MESSAGE',
      createdBy: notification.createdBy,
      recepients: notification.recepients,
      groups: notification.groups,
      canal: notification.canal,
      sendSms: notification.sendSms,
      sendNotification: notification.sendNotification,
      sendMail: notification.sendMail,
    });
  }

  previousState(): void {
    window.history.back();
  }

  getUsersDict(omitResetRecipients?: boolean): void {
    if (!omitResetRecipients) {
      this.editForm.get(['recepients'])?.reset();
      this.editForm.get(['groups'])?.reset();
    }

    switch (this.selectedNotificationCanal) {
      case ENotificationCanal.ALL:
        this.editForm.get(['recepients'])?.clearValidators();
        this.editForm.get(['groups'])?.clearValidators();
        this.editForm.get(['recepients'])?.updateValueAndValidity();
        this.editForm.get(['groups'])?.updateValueAndValidity();
        break;
      case ENotificationCanal.GROUPS:
        {
          const payload: any = {
            page: 0,
            size: 5000,
            sort: ['name,asc'],
          };
          this.userService.queryGroup(payload).subscribe((res: HttpResponse<IUserGroup[]>) => {
            this.groups = res.body || [];
            this.filterNotExistRecipients();
            this.editForm.get(['recepients'])?.clearValidators();
            this.editForm.get(['groups'])?.setValidators([Validators.required]);
            this.editForm.get(['recepients'])?.updateValueAndValidity();
            this.editForm.get(['groups'])?.updateValueAndValidity();
          });
        }
        break;
      case ENotificationCanal.USERS:
        if (this.selectedNotificationType === ENotificationType.NOTIFICATION) {
          const payload: any = {
            page: 0,
            size: 5000,
            sort: ['lastName,asc'],
          };
          this.userService.queryActive(payload).subscribe((res: HttpResponse<IUser[]>) => {
            this.users = res.body || [];
            this.filterNotExistRecipients();
          });
        } else if (this.selectedNotificationType === ENotificationType.SMS) {
          const payload: any = {
            page: 0,
            size: 5000,
            sort: ['lastName,asc'],
          };
          this.userService.queryWithPhone(payload).subscribe((res: HttpResponse<IUser[]>) => {
            this.users = res.body || [];
            this.filterNotExistRecipients();
          });
        }

        this.editForm.get(['recepients'])?.setValidators([Validators.required]);
        this.editForm.get(['groups'])?.clearValidators();
        this.editForm.get(['recepients'])?.updateValueAndValidity();
        this.editForm.get(['groups'])?.updateValueAndValidity();
        break;
    }
  }

  filterNotExistRecipients(): void {
    // THIS IS WORKAROUND
    // https://github.com/primefaces/primeng/issues/9673
    this.multiselectPlaceholder = 'Select';
    setTimeout(() => {
      this.multiselectPlaceholder = '';
    });
    // END WORKAROUND

    if (this.selectedNotificationType === ENotificationType.NOTIFICATION) {
      const recepients = this.editForm.get(['recepients'])?.value?.filter((recipient: any) => {
        return this.users.some(user => user.id === recipient.id);
      });

      this.editForm.get(['recepients'])?.patchValue(recepients ?? []);
    }

    if (this.selectedNotificationType === ENotificationType.MAIL) {
      const groups = this.editForm.get(['groups'])?.value?.filter((recipient: any) => {
        return this.users.some(user => user.id === recipient.id);
      });

      this.editForm.get(['groups'])?.patchValue(groups ?? []);
    }
  }

  save(): void {
    this.isSaving = true;
    const notification = this.createFromForm();

    if (notification.sendSms) {
      notification.description = this.stripHTML(notification.description || '');
    }
    if (notification.id !== undefined) {
      this.subscribeToSaveResponse(this.notificationService.update(notification));
    } else {
      this.subscribeToSaveResponse(this.notificationService.create(notification));
    }
  }

  private createFromForm(): INotification {
    let description = this.editForm.get(['description'])!.value;

    if (this.editForm.get(['sendSms'])!.value) {
      const escape = document.createElement('div');
      escape.innerHTML = description;
      description = escape.textContent || escape.innerHTML || '';
    }

    const notificationData = {
      ...new Notification(),
      id: this.editForm.get(['id'])!.value,
      subject: this.editForm.get(['subject'])!.value,
      description,
      createDate: this.editForm.get(['createDate'])!.value ? moment(this.editForm.get(['createDate'])!.value, DATE_TIME_FORMAT) : undefined,
      readDate: this.editForm.get(['readDate'])!.value ? moment(this.editForm.get(['readDate'])!.value, DATE_TIME_FORMAT) : undefined,
      status: this.editForm.get(['status'])!.value,
      category: notificationCategory.MESSAGE,
      canal:
        this.selectedNotificationCanal === ENotificationCanal.GROUPS
          ? notificationCanal.GROUPS
          : this.selectedNotificationCanal === ENotificationCanal.USERS
          ? notificationCanal.USERS
          : notificationCanal.ALL,
      createdBy: this.editForm.get(['createdBy'])!.value,
      recepients: this.editForm.get(['recepients'])!.value,
      groups: this.editForm.get(['groups'])!.value,
      sendSms: this.selectedNotificationType === ENotificationType.SMS,
      sendNotification: this.selectedNotificationType === ENotificationType.NOTIFICATION,
      sendMail: this.selectedNotificationType === ENotificationType.MAIL,
    };

    if (this.selectedNotificationCanal === ENotificationCanal.ALL) {
      // Dla wszystkich nie wysyłaj odbiorców i grup
      notificationData.recepients = [];
      notificationData.groups = [];
    } else if (this.selectedNotificationCanal === ENotificationCanal.USERS) {
      // Dla userów nie wysyłaj grup użytkowników
      notificationData.groups = [];
    } else if (this.selectedNotificationCanal === ENotificationCanal.GROUPS) {
      // Dla grup nie wysyłaj userów
      notificationData.recepients = [];
    }

    return notificationData;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotification>>): void {
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

  stripHTML(message: string): string {
    const tmp = document.createElement('DIV');
    tmp.innerHTML = message;
    return tmp.textContent || tmp.innerHTML || '';
  }
}
