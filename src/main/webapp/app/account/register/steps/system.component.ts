import { RegisterService } from '../register.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Message, MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Component({
  templateUrl: './system.component.html',
  styleUrls: ['../register.scss'],
  providers: [MessageService],
})
export class SystemDataComponent implements OnInit {
  personalData: any;
  msgs: Message[] = [];
  submitted = false;

  constructor(
    public registerService: RegisterService,
    private router: Router,
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
    this.router.navigate(['./account/register/personal']);
  }

  nextPage(): void {
    const password = this.personalData.password;
    if (password !== this.personalData.confirmPassword) {
      this.translateService
        .get('global.messages.error.dontmatch')
        .subscribe(translatedErrorMessage =>
          this.messageService.add({ severity: 'error', summary: 'Walidacja formularza', detail: translatedErrorMessage })
        );
    } else {
      this.registerService.accountInformation.personalData = this.personalData;
      this.router.navigate(['./account/register/terms']);
      return;
    }
    this.submitted = true;
  }
}
