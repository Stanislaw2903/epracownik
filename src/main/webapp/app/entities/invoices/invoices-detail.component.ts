import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IInvoices } from 'app/shared/model/invoices.model';

@Component({
  selector: 'jhi-invoices-detail',
  templateUrl: './invoices-detail.component.html',
})
export class InvoicesDetailComponent implements OnInit {
  invoices: IInvoices | null = null;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ invoices }) => (this.invoices = invoices));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType = '', base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }
}
