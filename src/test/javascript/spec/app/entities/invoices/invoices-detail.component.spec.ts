import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { EbokTestModule } from '../../../test.module';
import { InvoicesDetailComponent } from 'app/entities/invoices/invoices-detail.component';
import { Invoices } from 'app/shared/model/invoices.model';

describe('Component Tests', () => {
  describe('Invoices Management Detail Component', () => {
    let comp: InvoicesDetailComponent;
    let fixture: ComponentFixture<InvoicesDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ invoices: new Invoices(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [EbokTestModule],
        declarations: [InvoicesDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(InvoicesDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(InvoicesDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load invoices on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.invoices).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeContentType, fakeBase64);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeContentType, fakeBase64);
      });
    });
  });
});
