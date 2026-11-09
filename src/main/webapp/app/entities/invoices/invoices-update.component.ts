import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IInvoices, Invoices } from 'app/shared/model/invoices.model';
import { InvoicesService } from './invoices.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

@Component({
  selector: 'jhi-invoices-update',
  templateUrl: './invoices-update.component.html',
})
export class InvoicesUpdateComponent implements OnInit {
  isSaving = false;
  issueDateDp: any;
  paymentDeadlineDp: any;

  editForm = this.fb.group({
    id: [],
    issueDate: [],
    documentNumber: [],
    paymentDeadline: [],
    grossAmount: [],
    toPay: [],
    documentFile: [],
    documentFileContentType: [],
    status: [],
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected invoicesService: InvoicesService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ invoices }) => {
      this.updateForm(invoices);
    });
  }

  updateForm(invoices: IInvoices): void {
    this.editForm.patchValue({
      id: invoices.id,
      issueDate: invoices.issueDate,
      documentNumber: invoices.documentNumber,
      paymentDeadline: invoices.paymentDeadline,
      grossAmount: invoices.grossAmount,
      toPay: invoices.toPay,
      documentFile: invoices.documentFile,
      documentFileContentType: invoices.documentFileContentType,
      status: invoices.status,
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: any, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('ebokApp.error', { ...err, key: 'error.file.' + err.key })
      );
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const invoices = this.createFromForm();
    if (invoices.id !== undefined) {
      this.subscribeToSaveResponse(this.invoicesService.update(invoices));
    } else {
      this.subscribeToSaveResponse(this.invoicesService.create(invoices));
    }
  }

  private createFromForm(): IInvoices {
    return {
      ...new Invoices(),
      id: this.editForm.get(['id'])!.value,
      issueDate: this.editForm.get(['issueDate'])!.value,
      documentNumber: this.editForm.get(['documentNumber'])!.value,
      paymentDeadline: this.editForm.get(['paymentDeadline'])!.value,
      grossAmount: this.editForm.get(['grossAmount'])!.value,
      toPay: this.editForm.get(['toPay'])!.value,
      documentFileContentType: this.editForm.get(['documentFileContentType'])!.value,
      documentFile: this.editForm.get(['documentFile'])!.value,
      status: this.editForm.get(['status'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInvoices>>): void {
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
}
