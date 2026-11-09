import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IQualifications } from 'app/shared/model/qualifications.model';

@Component({
  selector: 'jhi-qualifications-detail',
  templateUrl: './qualifications-detail.component.html',
})
export class QualificationsDetailComponent implements OnInit {
  qualifications: IQualifications | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ qualifications }) => (this.qualifications = qualifications));
  }

  previousState(): void {
    window.history.back();
  }
}
