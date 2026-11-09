import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { QualificationsService } from 'app/entities/qualifications/qualifications.service';
import { IQualifications, Qualifications } from 'app/shared/model/qualifications.model';

describe('Service Tests', () => {
  describe('Qualifications Service', () => {
    let injector: TestBed;
    let service: QualificationsService;
    let httpMock: HttpTestingController;
    let elemDefault: IQualifications;
    let expectedResult: IQualifications | IQualifications[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(QualificationsService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Qualifications(0, 'AAAAAAA', currentDate, currentDate, 'AAAAAAA', false);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            startDate: currentDate.format(DATE_FORMAT),
            expireDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Qualifications', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            startDate: currentDate.format(DATE_FORMAT),
            expireDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            expireDate: currentDate,
          },
          returnedFromService
        );

        service.create(new Qualifications()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Qualifications', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            startDate: currentDate.format(DATE_FORMAT),
            expireDate: currentDate.format(DATE_FORMAT),
            comments: 'BBBBBB',
            subsidized: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            expireDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Qualifications', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            startDate: currentDate.format(DATE_FORMAT),
            expireDate: currentDate.format(DATE_FORMAT),
            comments: 'BBBBBB',
            subsidized: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            expireDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Qualifications', () => {
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
