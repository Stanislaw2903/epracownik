import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EbokTestModule } from '../../../test.module';
import { NotificationReadDetailComponent } from 'app/entities/notification-read/notification-read-detail.component';
import { NotificationRead } from 'app/shared/model/notification-read.model';

describe('Component Tests', () => {
  describe('NotificationRead Management Detail Component', () => {
    let comp: NotificationReadDetailComponent;
    let fixture: ComponentFixture<NotificationReadDetailComponent>;
    const route = ({ data: of({ notificationRead: new NotificationRead(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EbokTestModule],
        declarations: [NotificationReadDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(NotificationReadDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(NotificationReadDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load notificationRead on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.notificationRead).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
