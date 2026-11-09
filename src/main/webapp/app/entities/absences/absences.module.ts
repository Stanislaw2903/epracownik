import { EbokSharedModule } from 'app/shared/shared.module';
import { TableModule } from 'primeng/table';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbsencesComponent } from './absences.component';
import { RouterModule } from '@angular/router';
import { absencesRoute } from './absences.route';
import { PaginatorModule } from 'primeng/paginator';
import { CalendarModule } from 'primeng/calendar';
import { MultiSelectModule } from 'primeng/multiselect';

@NgModule({
  declarations: [AbsencesComponent],
  imports: [
    CommonModule,
    EbokSharedModule,
    RouterModule.forChild(absencesRoute),
    TableModule,
    MultiSelectModule,
    CalendarModule,
    PaginatorModule,
  ],
})
export class AbsencesModule {}
