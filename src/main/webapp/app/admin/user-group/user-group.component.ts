import { Component, OnInit } from '@angular/core';
import { ConfirmationService, LazyLoadEvent, MessageService } from 'primeng/api';
import { UserGroupService } from './user-group.service';
import { UserGroupStateService } from './user-group-state.service';
import { Router } from '@angular/router';
import { ToastService } from 'app/shared/toast/toast.service';

@Component({
  selector: 'jhi-user-group-list',
  templateUrl: './user-group.component.html',
  styleUrls: ['./user-group.component.scss'],
  providers: [ConfirmationService, MessageService],
})
export class UserGroupListComponent implements OnInit {
  userGroups: any[] = [];
  totalRecords = 0;
  loading = false;

  filters = {
    ids: '',
    name: '',
    code: '',
  };

  pageOptions = {
    page: 0,
    size: 10,
    sort: 'id,asc',
  };

  selectedGroup: any = null;

  constructor(
    private userGroupService: UserGroupService,
    private router: Router,
    private userGroupStateService: UserGroupStateService,
    private confirmationService: ConfirmationService,
    private _toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadUserGroups();
  }

  loadUserGroups(event?: LazyLoadEvent): void {
    this.loading = true;

    if (event) {
      this.pageOptions.page = event.first! / event.rows!;
      this.pageOptions.size = event.rows!;
      if (event.sortField) {
        this.pageOptions.sort = `${event.sortField},${event.sortOrder === 1 ? 'asc' : 'desc'}`;
      }
    }

    this.userGroupService
      .getUserGroups({
        ...this.filters,
        ...this.pageOptions,
      })
      .subscribe(data => {
        this.userGroups = data.content;
        this.totalRecords = data.totalElements;
        this.loading = false;
      });
  }

  editGroup(group: any): void {
    this.userGroupStateService.setGroup(group);
    this.router.navigate(['admin/user-groups/edit']);
  }

  confirmDelete(group: any): void {
    this.selectedGroup = group;

    this.confirmationService.confirm({
      message: `Czy na pewno chcesz usunąć grupę "${group.name}"?`,
      header: 'Potwierdzenie usunięcia',
      icon: 'pi pi-exclamation-triangle',
      accept: () => this.deleteGroup(),
      reject: () => {
        this.selectedGroup = null;
      },
    });
  }

  deleteGroup(): void {
    if (!this.selectedGroup) return;

    this.userGroupService.deleteUserGroup(this.selectedGroup.id).subscribe({
      next: () => {
        this._toastService.sendMessage({
          type: 'success',
          title: 'info',
          message: 'userGroups.messages.deleted',
        });
        this.selectedGroup = null;
        this.loadUserGroups({ first: 0, rows: this.pageOptions.size });
      },
    });
  }
}
