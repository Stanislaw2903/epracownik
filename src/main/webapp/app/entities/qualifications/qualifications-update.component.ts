import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IQualifications, Qualifications } from 'app/shared/model/qualifications.model';
import { QualificationsService } from './qualifications.service';

@Component({
  selector: 'jhi-qualifications-update',
  templateUrl: './qualifications-update.component.html',
})
export class QualificationsUpdateComponent implements OnInit {
  isSaving = false;
  startDateDp: any;
  expireDateDp: any;

  editForm = this.fb.group({
    id: [],
    name: [],
    startDate: [],
    expireDate: [],
    comments: [],
    subsidized: [],
  });

  constructor(protected qualificationsService: QualificationsService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ qualifications }) => {
      this.updateForm(qualifications);
    });
  }

  updateForm(qualifications: IQualifications): void {
    this.editForm.patchValue({
      id: qualifications.id,
      name: qualifications.name,
      startDate: qualifications.startDate,
      expireDate: qualifications.expireDate,
      comments: qualifications.comments,
      subsidized: qualifications.subsidized,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const qualifications = this.createFromForm();
    if (qualifications.id !== undefined) {
      this.subscribeToSaveResponse(this.qualificationsService.update(qualifications));
    } else {
      this.subscribeToSaveResponse(this.qualificationsService.create(qualifications));
    }
  }

  private createFromForm(): IQualifications {
    return {
      ...new Qualifications(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      startDate: this.editForm.get(['startDate'])!.value,
      expireDate: this.editForm.get(['expireDate'])!.value,
      comments: this.editForm.get(['comments'])!.value,
      subsidized: this.editForm.get(['subsidized'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQualifications>>): void {
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
