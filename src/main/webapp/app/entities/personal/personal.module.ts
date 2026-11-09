import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MedicalExaminationComponent } from './medical-examination/medical-examination.component';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { TableModule } from 'primeng/table';
import { CoursesComponent } from './courses/courses.component';
import { SafetyTrainingComponent } from './safety-training/safety-training.component';
import { QualificationsComponent } from './qualifications/qualifications.component';
import { PaginatorModule } from 'primeng/paginator';
import { EbokSharedModule } from 'app/shared/shared.module';

// RouterModule.forChild(invoicesRoute)

@NgModule({
  declarations: [MedicalExaminationComponent, CoursesComponent, SafetyTrainingComponent, QualificationsComponent],
  imports: [
    CommonModule,
    EbokSharedModule,
    TableModule,
    PaginatorModule,
    RouterModule.forChild([
      {
        path: 'medical',
        component: MedicalExaminationComponent,
        data: {
          authorities: [Authority.USER],
          defaultSort: 'dateTo,asc',
          pageTitle: 'global.menu.entities.medicalExamination',
        },
        canActivate: [UserRouteAccessService],
      },
      {
        path: 'courses',
        component: CoursesComponent,
        data: {
          authorities: [Authority.USER],
          defaultSort: 'completionDate,asc',
          pageTitle: 'global.menu.entities.coursesAndTraining',
        },
        canActivate: [UserRouteAccessService],
      },
      {
        path: 'qualifications',
        component: QualificationsComponent,
        data: {
          authorities: [Authority.USER],
          defaultSort: 'dateTo,asc',
          pageTitle: 'global.menu.entities.permissions',
        },
        canActivate: [UserRouteAccessService],
      },
      {
        path: 'safetyTraining',
        component: SafetyTrainingComponent,
        data: {
          authorities: [Authority.USER],
          defaultSort: 'dateTo,asc',
          pageTitle: 'global.menu.entities.safetyTraining',
        },
        canActivate: [UserRouteAccessService],
      },
    ]),
  ],
})
export class PersonalModule {}
