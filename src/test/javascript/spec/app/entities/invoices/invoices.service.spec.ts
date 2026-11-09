import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { InvoicesService } from 'app/entities/invoices/invoices.service';
import { IInvoices, Invoices } from 'app/shared/model/invoices.model';

describe('Service Tests', () => {
  describe('Invoices Service', () => {
    let injector: TestBed;
    let service: InvoicesService;
    let httpMock: HttpTestingController;
    let elemDefault: IInvoices;
    let expectedResult: IInvoices | IInvoices[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(InvoicesService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Invoices(0, currentDate, 'AAAAAAA', currentDate, 0, 0, 'image/png', 'AAAAAAA', false);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            issueDate: currentDate.format(DATE_FORMAT),
            paymentDeadline: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Invoices', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            issueDate: currentDate.format(DATE_FORMAT),
            paymentDeadline: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            issueDate: currentDate,
            paymentDeadline: currentDate,
          },
          returnedFromService
        );

        service.create(new Invoices()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Invoices', () => {
        const returnedFromService = Object.assign(
          {
            issueDate: currentDate.format(DATE_FORMAT),
            documentNumber: 'BBBBBB',
            paymentDeadline: currentDate.format(DATE_FORMAT),
            grossAmount: 1,
            toPay: 1,
            documentFile: 'BBBBBB',
            status: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            issueDate: currentDate,
            paymentDeadline: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Invoices', () => {
        const returnedFromService = Object.assign(
          {
            issueDate: currentDate.format(DATE_FORMAT),
            documentNumber: 'BBBBBB',
            paymentDeadline: currentDate.format(DATE_FORMAT),
            grossAmount: 1,
            toPay: 1,
            documentFile: 'BBBBBB',
            status: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            issueDate: currentDate,
            paymentDeadline: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Invoices', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
