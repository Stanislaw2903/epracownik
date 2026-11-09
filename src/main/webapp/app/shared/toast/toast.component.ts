import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { compact, isObject } from 'lodash';
import { ToastService } from './toast.service';

@Component({
  selector: 'jhi-toast',
  templateUrl: './toast.component.html',
  providers: [MessageService],
})
export class ToastComponent implements OnInit, OnDestroy {
  private _toastErrorSubscription$: Subject<boolean> = new Subject();

  constructor(private messageService: MessageService, public _toastService: ToastService, public translate: TranslateService) {}

  ngOnInit(): void {
    this.subscribeToMessageError();
  }
  ngOnDestroy(): void {
    this._toastErrorSubscription$.next();
    this._toastErrorSubscription$.complete();
  }

  addSingleMessage(message: any): void {
    this.translateString([message.message, message.title]).then(translatedValues => {
      if (typeof translatedValues === 'object') {
        this.messageService.add({
          severity: message.type,
          detail: translatedValues[0],
          summary: translatedValues[1],
          life: 5000,
        });
      }
    });
  }

  addMessageFormError(message: any): void {
    const listField = Object.keys(message);

    // TODO: All for change
    listField.forEach(fieldName => {
      this.translateString(`fields.${fieldName}`).then(translatedField => {
        this.translateString(`errors.${message[fieldName]}`).then(translatedValue => {
          const notTranslatedString = (translatedValue as string).search('errors.') !== -1;
          this.translateFieldError(translatedField as string).then(translatedSummary => {
            this.messageService.add({
              severity: 'error',
              summary: translatedSummary,
              detail: notTranslatedString ? message[fieldName] : translatedValue,
              life: 5000,
            });
          });
        });
      });
    });
  }

  translateString(translateValue: string | string[]): Promise<string | string[]> {
    if (typeof translateValue === 'string') {
      return new Promise(resolve => {
        this.translate.get(translateValue).subscribe(translated => {
          resolve(translated);
        });
      });
    } else {
      return new Promise(resolve => {
        Promise.all(compact(translateValue).map(value => this.translate.get(value).toPromise())).then(values => {
          resolve(values);
        });
      });
    }
  }

  translateFieldError(translatedField: string): Promise<string> {
    return new Promise(resolve => {
      this.translate
        .get('rootFieldError', {
          fieldName: translatedField,
        })
        .subscribe(translatedError => {
          resolve(translatedError);
        });
    });
  }

  private subscribeToMessageError(): void {
    this._toastService
      .getMessage()
      .pipe(takeUntil(this._toastErrorSubscription$))
      .subscribe((message: any) => {
        if (message.message) {
          this.addSingleMessage(message);
        } else if (isObject(message.error)) {
          this.addMessageFormError(message.error);
        }
      });
  }
}
