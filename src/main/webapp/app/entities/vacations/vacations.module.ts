import { DialogModule } from 'primeng/dialog';
import { RouterModule } from '@angular/router';
import { EbokSharedModule } from 'app/shared/shared.module';
import { NgModule } from '@angular/core';
import { VacationsComponent } from './vacations.component';
import { vacationRoute } from './vacations.route';
import { TableModule } from 'primeng/table';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { PaginatorModule } from 'primeng/paginator';
import { TooltipModule } from 'primeng/tooltip';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { MultiSelectModule } from 'primeng/multiselect';
import { ToolbarModule } from 'primeng/toolbar';

@NgModule({
  declarations: [VacationsComponent],
  imports: [
    EbokSharedModule,
    RouterModule.forChild(vacationRoute),
    TableModule,
    DropdownModule,
    TooltipModule,
    CalendarModule,
    DialogModule,
    ScrollPanelModule,
    PaginatorModule,
    MultiSelectModule,
    ToolbarModule,
  ],
})
export class VacationsModule {}
