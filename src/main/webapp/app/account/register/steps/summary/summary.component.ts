import { RegisterService } from 'app/account/register/register.service';
import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { JhiLanguageService } from 'ng-jhipster';
import { HttpErrorResponse } from '@angular/common/http';
import { EMAIL_ALREADY_USED_TYPE } from 'app/shared/constants/error.constants';
import { Message, MessageService } from 'primeng/api';

@Component({
  selector: 'jhi-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['../../register.scss'],
  providers: [MessageService],
})
export class SummaryComponent implements OnInit {
  personalData: any;
  msgs: Message[] = [];
  success = false;
  constructor(
    public registerService: RegisterService,
    private router: Router,
    private languageService: JhiLanguageService,
    private messageService: MessageService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.personalData = this.registerService.getAccountInformation().personalData;
  }

  cancel(): void {
    this.router.navigate(['login']);
  }

  prevPage(): void {
    this.router.navigate(['./account/register/terms']);
  }

  register(): void {
    this.personalData.langKey = this.languageService.getCurrentLanguage();
    this.registerService.save(this.personalData).subscribe(
      () =>
        this.translateService
          .get('register.messages.success')
          .subscribe(translatedErrorMessage => this.messageService.add({ severity: 'success', detail: translatedErrorMessage })),
      response => this.processError(response)
    );
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.translateService
        .get('register.messages.error.emailexist')
        .subscribe(translatedErrorMessage =>
          this.messageService.add({ severity: 'error', summary: 'Walidacja formularza', detail: translatedErrorMessage })
        );
    } else {
      this.translateService
        .get('register.messages.error.fail')
        .subscribe(translatedErrorMessage => this.messageService.add({ severity: 'error', detail: translatedErrorMessage }));
    }
  }
}
