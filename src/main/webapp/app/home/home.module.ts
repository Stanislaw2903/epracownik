import { DialogModule } from 'primeng/dialog';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { SliderModule } from 'primeng/slider';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { EditorModule } from 'primeng/editor';
import { EbokSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { MenuModule } from 'primeng/menu';
import { ToolbarModule } from 'primeng/toolbar';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SelectButtonModule } from 'primeng/selectbutton';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { UserVacationComponent } from './user-vacation/user-vacation.component';
import { PaginatorModule } from 'primeng/paginator';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { NgApexchartsModule } from 'ng-apexcharts';

@NgModule({
  imports: [
    EbokSharedModule,
    ButtonModule,
    CardModule,
    ChartModule,
    SliderModule,
    TableModule,
    DialogModule,
    TooltipModule,
    MenuModule,
    SelectButtonModule,
    DropdownModule,
    CalendarModule,
    ScrollPanelModule,
    ToolbarModule,
    FormsModule,
    PaginatorModule,
    ConfirmDialogModule,
    ReactiveFormsModule,
    EditorModule,
    NgApexchartsModule,
    RouterModule.forChild(HOME_ROUTE),
  ],
  declarations: [HomeComponent, UserVacationComponent],
})
export class EbokHomeModule {}
