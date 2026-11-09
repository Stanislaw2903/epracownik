import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { EbokTestModule } from '../../../test.module';
import { QualificationsUpdateComponent } from 'app/entities/qualifications/qualifications-update.component';
import { QualificationsService } from 'app/entities/qualifications/qualifications.service';
import { Qualifications } from 'app/shared/model/qualifications.model';

describe('Component Tests', () => {
  describe('Qualifications Management Update Component', () => {
    let comp: QualificationsUpdateComponent;
    let fixture: ComponentFixture<QualificationsUpdateComponent>;
    let service: QualificationsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EbokTestModule],
        declarations: [QualificationsUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(QualificationsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(QualificationsUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(QualificationsService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Qualifications(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Qualifications();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
