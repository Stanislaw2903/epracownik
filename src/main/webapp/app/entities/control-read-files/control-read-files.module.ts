import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { EbokSharedModule } from 'app/shared/shared.module';
import { CalendarModule } from 'primeng/calendar';
import { MultiSelectModule } from 'primeng/multiselect';
import { PaginatorModule } from 'primeng/paginator';
import { TableModule } from 'primeng/table';
import { ControlReadFilesComponent } from './control-read-files.component';
import { controlReadFilesRoute } from './control-read-files.route';
import { InputNumberModule } from 'primeng/inputnumber';

@NgModule({
  declarations: [ControlReadFilesComponent],
  imports: [
    CommonModule,
    EbokSharedModule,
    RouterModule.forChild(controlReadFilesRoute),
    TableModule,
    MultiSelectModule,
    CalendarModule,
    PaginatorModule,
    InputNumberModule,
  ],
})
export class ControlReadFilesModule {}
