import { HttpHeaders } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription, combineLatest } from 'rxjs';
import { ResendActivationComponent } from './resend-activation/resend-activation.component';
import { SessionStorageService } from 'ngx-webstorage';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IUser, User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { IPagablePagination, IPagableSort } from 'app/shared/model/pagable.model';
import { ConfirmationService } from 'primeng/api';
import { IntegrateUsersComponent } from './integrate-users/integrate-users.component';
import { UserManagementDeleteDialogComponent } from './user-management-delete-dialog.component';

import { TranslateService } from '@ngx-translate/core';
import { ToastService } from 'app/shared/toast/toast.service';
import { isEmpty, isNil, orderBy, values } from 'lodash';
import * as moment from 'moment';
import * as pdfMake from 'pdfmake/build/pdfmake';
import * as pdfFonts from 'pdfmake/build/vfs_fonts';
(pdfMake as any).vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.scss'],
  providers: [ConfirmationService],
})
export class UserManagementComponent implements OnInit, OnDestroy {
  currentAccount: Account | null = null;
  users: User[] = [];
  loading = true;
  vacationApproveLevel = '';
  selectedUserRelatedUsers!: number;
  userDict: any;
  filters: any = {};
  dataFromStorage: any;

  // Table configuration
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage!: number;
  predicate!: string;
  ascending!: boolean;
  page!: number;

  displayIntegrateUser = false;

  currentShowOnlyAssignedUser!: boolean;

  userGroups: any[] = this.activatedRoute.snapshot.data.userGroup;

  typeDict = [
    {
      label: 'Pracownik',
      value: 'P',
    },
    {
      label: 'Zleceniobiorca',
      value: 'O',
    },
  ];

  activeUserDict = [
    {
      label: 'Tak',
      value: true,
    },
    {
      label: 'Nie',
      value: false,
    },
  ];

  constructor(
    public userService: UserService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private eventManager: JhiEventManager,
    private _toastService: ToastService,
    private translateService: TranslateService,
    private confirmationService: ConfirmationService,
    private modalService: NgbModal,
    private $sessionStorage: SessionStorageService
  ) {
    this.dataFromStorage = sessionStorage.getItem('userManagement');

    if (!isEmpty(this.dataFromStorage) && this.dataFromStorage !== null) {
      this.dataFromStorage = JSON.parse(this.dataFromStorage);
    } else {
      this.dataFromStorage = {};
    }

    this.vacationApproveLevel = this.$sessionStorage.retrieve('vacationApproveLevel');

    this.filters.login = this.dataFromStorage.filters?.login?.value ?? '';
    this.filters.firstName = this.dataFromStorage.filters?.firstName?.value ?? '';
    this.filters.lastName = this.dataFromStorage.filters?.lastName?.value ?? '';
    this.filters.email = this.dataFromStorage.filters?.email?.value ?? '';
    this.filters.phoneNumber = this.dataFromStorage.filters?.phoneNumber?.value ?? '';

    this.handleNavigation();
    this.userService.getDictAllUsers().subscribe((res: IUser[]) => {
      this.userDict = orderBy(res, ['name'], ['asc']) || [];
    });
  }

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));
    this.registerChangeInQualifications();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  protected handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const pageNumber = 0;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      this.itemsPerPage =
        params.get('size') !== undefined || params.get('size') !== null ? parseInt(params.get('size') ?? '10', 10) : ITEMS_PER_PAGE;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(
          {
            page: pageNumber,
            rows: this.itemsPerPage,
          },
          false
        );
      }
    }).subscribe();
  }

  registerChangeInQualifications(): void {
    this.eventSubscriber = this.eventManager.subscribe('userListModification', () => this.loadPage());
  }

  loadPage(paginaton?: IPagablePagination, dontNavigate?: boolean): void {
    if (paginaton === undefined) {
      this.page = 0;
    } else {
      this.itemsPerPage = paginaton.rows;
    }

    this.currentShowOnlyAssignedUser = !!this.selectedUserRelatedUsers;

    const pageToLoad: number = paginaton && !isNil(paginaton?.page) ? paginaton.page : this.page || 0;

    const payload: any = {
      page: pageToLoad,
      size: paginaton?.rows || this.itemsPerPage,
      sort: this.sort(),
    };

    if (this.selectedUserRelatedUsers) {
      payload.related = this.selectedUserRelatedUsers;
    }

    if (this.filters.login) {
      payload.login = this.filters.login;
    } else {
      delete payload.login;
      delete this.dataFromStorage.filters?.login;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    if (this.filters.firstName) {
      payload.firstName = this.filters.firstName;
    } else {
      delete payload.firstName;
      delete this.dataFromStorage.filters?.firstName;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    if (this.filters.lastName) {
      payload.lastName = this.filters.lastName;
    } else {
      delete payload.lastName;
      delete this.dataFromStorage.filters?.lastName;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    if (this.filters.email) {
      payload.email = this.filters.email;
    } else {
      delete payload.email;
      delete this.dataFromStorage.filters?.email;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    if (this.filters.phoneNumber) {
      payload.phoneNumber = this.filters.phoneNumber;
    } else {
      delete payload.phoneNumber;
      delete this.dataFromStorage.filters?.phoneNumber;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    if (this.filters.type) {
      payload.type = this.filters.type;
    } else {
      delete payload.type;
      delete this.dataFromStorage.filters?.type;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    if (this.filters.groups) {
      payload.groups = this.filters.groups;
    } else {
      delete payload.groups;
      delete this.dataFromStorage.filters?.groups;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    if (this.filters.active !== null && this.filters.active !== undefined && this.filters.active !== '') {
      payload.activated = this.filters.active;
    } else {
      delete payload.activated;
      delete this.dataFromStorage.filters?.active;
      sessionStorage.setItem('userManagement', JSON.stringify(this.dataFromStorage));
    }

    this.userService.query(payload).subscribe((res: any) => {
      return this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
    });
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  customSort(sortData: IPagableSort): void {
    this.filters.login = sortData.filters.login?.value ?? '';
    this.filters.firstName = sortData.filters.firstName?.value ?? '';
    this.filters.lastName = sortData.filters.lastName?.value ?? '';
    this.filters.email = sortData.filters.email?.value ?? '';
    this.filters.phoneNumber = sortData.filters.phoneNumber?.value ?? '';
    this.filters.type = sortData.filters.type?.value ?? '';
    this.filters.groups = sortData.filters.groups?.value ?? '';
    this.filters.active = sortData.filters.active?.value ?? '';

    if (!this.loading) {
      if (sortData.sortField) {
        this.predicate = sortData.sortField;
        this.ascending = sortData.sortOrder === 1;
      }
      this.loadPage({
        page: this.page,
        rows: sortData.rows,
      });
    }
  }

  protected onSuccess(data: User[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/admin/user-management'], {
        queryParams: {
          page: 0,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.loading = false;
    this.users = (data as User[]) || [];
  }

  onChangeAssignedUserDropdown(): void {
    if (this.selectedUserRelatedUsers === null && this.currentShowOnlyAssignedUser) {
      this.loadPage();
    }
  }

  setActive(user: User, isActivated: boolean): void {
    this.userService.update({ ...user, activated: isActivated }).subscribe(() => this.loadPage());
  }

  trackIdentity(index: number, item: User): any {
    return item.id;
  }

  deleteUser(user: User): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.user = user;
  }

  resendActivationEmail(): void {
    this.confirmationService.confirm({
      message: 'Czy jesteś pewien, że chcesz wysłać ponownie e-maile aktywacyjne?',
      accept: () => {
        this.modalService.open(ResendActivationComponent, { size: 'lg', backdrop: 'static' });
      },
    });
  }

  integrateUsers(): void {
    this.userService.integrateWithMandory = false;
    this.displayIntegrateUser = true;
  }

  onConfirmIntegrate(): void {
    this.modalService.open(IntegrateUsersComponent, { size: 'lg', backdrop: 'static' });
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
      },
    });
  }

  exportPdf(): void {
    const columns = values(
      this.translateService.instant([
        'global.field.externalId',
        'userManagement.login',
        'userManagement.firstName',
        'userManagement.lastName',
        'userManagement.createdDate',
      ])
    );

    const data = this.users.map(user => {
      return [
        user.externalId ?? '',
        user.login ?? '',
        user.firstName ?? '',
        user.lastName ?? '',
        user.createdDate ? moment(user.createdDate).format('DD-MM-YYYY') : '',
      ];
    });

    const documentDefinition = {
      pageMargins: [20, 60, 20, 60],
      header: [
        { text: `Użytkownicy`, alignment: 'center', margin: [0, 20, 0, 8] },
        { text: `Wygenerowano dnia ${moment().format('DD MMM YYYY HH:mm:ss')}`, alignment: 'center', fontSize: 7 },
      ],
      content: [
        {
          layout: 'lightHorizontalLines',
          fontSize: 8,
          table: {
            headerRows: 1,
            widths: new Array(5).fill('*'),
            body: [columns, ...data],
          },
        },
      ],
      footer: this.footerPdf,
    };
    pdfMake.createPdf(documentDefinition as any).download('uzytkownicy.pdf');
  }

  exportExcel(): void {
    const columns = values(
      this.translateService.instant([
        'global.field.externalId',
        'userManagement.login',
        'userManagement.firstName',
        'userManagement.lastName',
        'userManagement.createdDate',
      ])
    );

    const rows = this.users.map(user => [
      user.externalId ?? '',
      user.login ?? '',
      user.firstName ?? '',
      user.lastName ?? '',
      user.createdDate ? moment(user.createdDate).format('DD-MM-YYYY') : '',
    ]);

    const escape = (val: any): string =>
      String(val ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');

    const headerHtml = `<tr>${columns.map(col => `<th>${escape(col)}</th>`).join('')}</tr>`;
    const bodyHtml = rows.map(row => `<tr>${row.map(cell => `<td>${escape(cell)}</td>`).join('')}</tr>`).join('');
    const table = `<table border="1"><thead>${headerHtml}</thead><tbody>${bodyHtml}</tbody></table>`;
    const html = `<html xmlns:x="urn:schemas-microsoft-com:office:excel"><head><meta charset="UTF-8"></head><body>${table}</body></html>`;

    const blob = new Blob(['﻿', html], { type: 'application/vnd.ms-excel' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'uzytkownicy.xls';
    link.click();
    URL.revokeObjectURL(url);
  }

  footerPdf(currentPage: any, pageCount: any): any {
    return { text: `Strona ${currentPage.toString()} z ${pageCount}`, alignment: 'center', margin: [0, 20, 0, 8], fontSize: 7 };
  }

  filter(filterCallback: any, value: any): void {
    filterCallback(value); // Przekazuje wybraną wartość do mechanizmu filtrowania PrimeNG.
  }
}
