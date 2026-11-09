import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { EbokTestModule } from '../../../test.module';
import { NotificationReadComponent } from 'app/entities/notification-read/notification-read.component';
import { NotificationReadService } from 'app/entities/notification-read/notification-read.service';
import { NotificationRead } from 'app/shared/model/notification-read.model';

describe('Component Tests', () => {
  describe('NotificationRead Management Component', () => {
    let comp: NotificationReadComponent;
    let fixture: ComponentFixture<NotificationReadComponent>;
    let service: NotificationReadService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EbokTestModule],
        declarations: [NotificationReadComponent],
      })
        .overrideTemplate(NotificationReadComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NotificationReadComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NotificationReadService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new NotificationRead(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.notificationReads && comp.notificationReads[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
