import { Component, OnInit } from '@angular/core';
import { ToastService } from 'app/shared/toast/toast.service';

import { ConfigurationService, Bean, PropertySource } from './configuration.service';

@Component({
  selector: 'jhi-configuration',
  templateUrl: './configuration.component.html',
})
export class ConfigurationComponent implements OnInit {
  allBeans!: Bean[];
  beans: Bean[] = [];
  beansFilter = '';
  beansAscending = true;
  propertySources: PropertySource[] = [];
  availableVacaationButton!: boolean;

  get availableVacationButtonStatus(): string {
    return this.availableVacaationButton ? 'enabled' : 'disabled';
  }

  constructor(private configurationService: ConfigurationService, private _toastService: ToastService) {}

  ngOnInit(): void {
    this.configurationService.getBeans().subscribe(beans => {
      this.allBeans = beans;
      this.filterAndSortBeans();
    });

    this.configurationService.getVacationButtonStatus().subscribe(status => {
      this.availableVacaationButton = status;
    });

    this.configurationService.getPropertySources().subscribe(propertySources => (this.propertySources = propertySources));
  }

  filterAndSortBeans(): void {
    this.beans = this.allBeans
      .filter(bean => !this.beansFilter || bean.prefix.toLowerCase().includes(this.beansFilter.toLowerCase()))
      .sort((a, b) => (a.prefix < b.prefix ? (this.beansAscending ? -1 : 1) : this.beansAscending ? 1 : -1));
  }

  sendStatusVacationButton(): void {
    this.configurationService.saveVacationButtonStatus(this.availableVacaationButton).subscribe(
      () => {
        this._toastService.sendMessage({
          type: 'success',
          title: 'info',
          message: 'configuration.availableVacationButton.successSend',
        });
      },
      () => {
        this._toastService.sendMessage({
          type: 'error',
          title: 'info',
          message: 'configuration.availableVacationButton.errorSend',
        });
      }
    );
  }
}
