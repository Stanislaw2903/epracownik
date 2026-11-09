import { RegisterService } from '../register.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SelectItem } from 'primeng/api/selectitem';

@Component({
  templateUrl: './personal.component.html',
  styleUrls: ['../register.scss'],
})
export class PersonalComponent implements OnInit {
  personalData: any;
  clientTypes: SelectItem[];
  submitted = false;

  constructor(public registerService: RegisterService, private router: Router) {
    this.clientTypes = [
      { label: 'Klient indywidualny', value: 'individual' },
      { label: 'Klient biznesowy', value: 'busines' },
    ];
  }

  ngOnInit(): void {
    this.personalData = this.registerService.getAccountInformation().personalData;
  }

  cancel(): void {
    this.router.navigate(['login']);
  }

  nextPage(): void {
    if (this.personalData.clientNumber && this.personalData.pesel && this.personalData.phone) {
      this.registerService.accountInformation.personalData = this.personalData;
      this.router.navigate(['./account/register/system']);

      return;
    }

    this.submitted = true;
  }
}
