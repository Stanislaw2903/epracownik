import { graphicRoute } from './graphic.route';
import { NgModule } from '@angular/core';
import { GraphicComponent } from './graphic.component';
import { RouterModule } from '@angular/router';
import { EbokSharedModule } from 'app/shared/shared.module';
import { CalendarModule } from 'primeng/calendar';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { PaginatorModule } from 'primeng/paginator';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectButtonModule } from 'primeng/selectbutton';

@NgModule({
  declarations: [GraphicComponent],
  imports: [
    EbokSharedModule,
    RouterModule.forChild(graphicRoute),
    TableModule,
    DropdownModule,
    TooltipModule,
    CalendarModule,
    InputNumberModule,
    SelectButtonModule,
    DialogModule,
    ScrollPanelModule,
    PaginatorModule,
  ],
})
export class GraphicModule {}
