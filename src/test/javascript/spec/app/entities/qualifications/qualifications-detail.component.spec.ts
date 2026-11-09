import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EbokTestModule } from '../../../test.module';
import { QualificationsDetailComponent } from 'app/entities/qualifications/qualifications-detail.component';
import { Qualifications } from 'app/shared/model/qualifications.model';

describe('Component Tests', () => {
  describe('Qualifications Management Detail Component', () => {
    let comp: QualificationsDetailComponent;
    let fixture: ComponentFixture<QualificationsDetailComponent>;
    const route = ({ data: of({ qualifications: new Qualifications(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EbokTestModule],
        declarations: [QualificationsDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(QualificationsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(QualificationsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load qualifications on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.qualifications).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
