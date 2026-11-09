import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { EbokTestModule } from '../../../test.module';
import { NotificationReadUpdateComponent } from 'app/entities/notification-read/notification-read-update.component';
import { NotificationReadService } from 'app/entities/notification-read/notification-read.service';
import { NotificationRead } from 'app/shared/model/notification-read.model';

describe('Component Tests', () => {
  describe('NotificationRead Management Update Component', () => {
    let comp: NotificationReadUpdateComponent;
    let fixture: ComponentFixture<NotificationReadUpdateComponent>;
    let service: NotificationReadService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EbokTestModule],
        declarations: [NotificationReadUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(NotificationReadUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NotificationReadUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NotificationReadService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new NotificationRead(123);
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
        const entity = new NotificationRead();
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
