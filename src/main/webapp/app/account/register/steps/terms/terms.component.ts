import { RegisterService } from 'app/account/register/register.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'jhi-terms',
  templateUrl: './terms.component.html',
  styleUrls: ['../../register.scss'],
})
export class TermsComponent implements OnInit {
  personalData: any;
  constructor(public registerService: RegisterService, private router: Router) {}

  ngOnInit(): void {
    this.personalData = this.registerService.getAccountInformation().personalData;
  }

  cancel(): void {
    this.router.navigate(['login']);
  }

  prevPage(): void {
    this.router.navigate(['./account/register/system']);
  }

  nextPage(): void {
    this.registerService.accountInformation.personalData = this.personalData;
    this.router.navigate(['./account/register/summary']);
  }
}
