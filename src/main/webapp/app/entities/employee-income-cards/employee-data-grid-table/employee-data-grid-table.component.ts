import { Component, OnInit } from '@angular/core';

import { IEmployee } from './../../../shared/model/employee.model';

@Component({
  selector: 'jhi-employee-data-grid-table',
  templateUrl: './employee-data-grid-table.component.html',
  styleUrls: ['./employee-data-grid-table.component.scss'],
})
export class EmployeeDataGridTableComponent implements OnInit {
  employee: IEmployee = {
    name: 'Ryszard',
    secondName: 'Krzysztof',
    surname: 'Adamczyk',
    pesel: 65020902433,
    fathersName: 'Zbigniew',
    mothersName: 'Janina',
    dateOfEmploy: '02-01-2015',
    dateOfDismissal: '03-09-2020',
    dateOfBirth: '09-02-2015',
    birthplace: 'Warszawa',
  };

  constructor() {}

  ngOnInit(): void {}
}
