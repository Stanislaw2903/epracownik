import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { SessionStorageService } from 'ngx-webstorage';
import { LANGUAGES } from 'app/core/language/language.constants';
import { User } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { UserGroupService } from '../user-group/user-group.service';
import { map } from 'rxjs/operators';

interface Select {
  label: string;
  value: string;
}
@Component({
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html',
})
export class UserManagementUpdateComponent implements OnInit {
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

  user!: User;
  languages: Select[] = LANGUAGES.map(authority => {
    return { label: authority, value: authority };
  });
  authorities: Select[] = [];
  isSaving = false;
  permissionVacation: Select[] = [];
  userGroups: any[] = [];
  userList: Select[] = [];
  isEditMode!: boolean;
  isUserFromIntegration!: boolean;
  vacationApproveLevel = '';
  approvalVerificationType = '';
  approvalType = '';
  approvalVerificationTypes: Select[] = [];
  approvalTypes: Select[] = [];

  editForm = this.fb.group({
    id: [],
    externalId: [''],
    login: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    firstName: ['', [Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    activated: [],
    groupId: [],
    langKey: [],
    authorizationToAcceptVacation: [],
    approvalVerificationType: [],
    approvalType: [],
    groups: [],
    type: [],
    relatedUsers: [],
    phoneNumber: ['', [Validators.pattern('([+]{0,1})([0-9]{9,15})')]],
    authorities: [null, [Validators.required]],
  });

  constructor(
    private userService: UserService,
    private userGroupService: UserGroupService,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private $sessionStorage: SessionStorageService
  ) {}

  ngOnInit(): void {
    this.vacationApproveLevel = this.$sessionStorage.retrieve('vacationApproveLevel');
    this.userService.approvalTypes().subscribe(approvalTypeList => {
      this.approvalTypes = approvalTypeList as Select[];
    });
    this.userService.approvalVerificationTypes().subscribe(approvalVerificationTypeList => {
      this.approvalVerificationTypes = approvalVerificationTypeList as Select[];
    });
    this.userService.permissionVacation().subscribe(permissionList => {
      this.permissionVacation = permissionList as Select[];
    });
    this.userGroupService
      .getUserGroups({
        size: 5000,
      })
      .pipe(map(result => result.content))
      .subscribe(userGroups => {
        this.userGroups = userGroups;
      });
    this.route.data.subscribe(({ user }) => {
      this.isEditMode = !!user.id;
      if (user) {
        this.user = user;
        if (this.user.id === undefined) {
          this.user.activated = true;
        }

        if (this.isEditMode) {
          this.userService.getRelatedUser(this.user.id).subscribe(relatedUsers => {
            const usersLists = relatedUsers.map((relatedUser: any) => {
              const userName = relatedUser.firstName || relatedUser.lastName ? relatedUser.firstName + ' ' + relatedUser.lastName : '';
              return {
                lastName: relatedUser.lastName,
                label: userName + ' - ' + relatedUser.login + ' - ID KiP: ' + relatedUser.id,
                value: relatedUser.id,
              };
            });

            const alphabetSort = (a: any, b: any) => {
              if (a.lastName > b.lastName) {
                return 1;
              }

              if (a.lastName < b.lastName) {
                return -1;
              }

              return 0;
            };

            this.userList = [
              ...usersLists.filter((x: any) => !!this.user.relatedUsers!.includes(x.value)).sort(alphabetSort),
              ...usersLists.filter((x: any) => !this.user.relatedUsers!.includes(x.value)).sort(alphabetSort),
            ];

            setTimeout(() => {
              this.updateForm(user);
            });
          });

          this.isUserFromIntegration = this.userFromIntegration();

          if (this.isUserFromIntegration && user.authorities.indexOf('ROLE_SYSTEM') === -1) {
            this.editForm.controls['login'].disable();
            this.editForm.controls['firstName'].disable();
            this.editForm.controls['lastName'].disable();
          }
        }

        this.userService.authorities().subscribe(authorities => {
          this.authorities = authorities.map(authority => {
            return { label: authority, value: authority };
          });
          setTimeout(() => {
            this.updateForm(user);
          });
        });
      }
    });
  }

  userFromIntegration(): boolean {
    return !this.user.manualInsert! && this.user.externalId;
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.updateUser(this.user);
    if (this.user.id !== undefined) {
      this.userService.update(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    } else {
      this.userService.create(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    }
  }

  private updateForm(user: User): void {
    this.editForm.patchValue({
      id: user.id,
      externalId: user.externalId,
      login: user.login,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      activated: user.activated,
      langKey: user.langKey,
      authorities: user.authorities,
      authorizationToAcceptVacation: user.authorizationToAcceptVacation,
      approvalVerificationType: user.approvalVerificationType,
      approvalType: user.approvalType,
      groups: user.groups,
      groupId: user.groupId,
      relatedUsers: user.relatedUsers,
      phoneNumber: user.phoneNumber,
      type: user.type,
    });
  }

  private updateUser(user: User): void {
    user.login = this.editForm.get(['login'])!.value;
    user.externalId = this.editForm.get(['externalId'])!.value;
    user.firstName = this.editForm.get(['firstName'])!.value;
    user.lastName = this.editForm.get(['lastName'])!.value;
    user.email = this.editForm.get(['email'])!.value;
    user.activated = this.editForm.get(['activated'])!.value;
    user.groupId = this.editForm.get(['groupId'])!.value;
    user.langKey = this.editForm.get(['langKey'])!.value;
    user.authorities = this.editForm.get(['authorities'])!.value;
    user.phoneNumber = this.editForm.get(['phoneNumber'])!.value;
    if (!this.isEditMode) {
      user.type = this.editForm.get(['type'])!.value;
      console.log(this.editForm.get(['type'])!.value);
      console.log(user.type);
    }
    user.groups = this.editForm.get(['groups'])!.value;
    if (this.isEditMode) {
      user.authorizationToAcceptVacation = this.editForm.get(['authorizationToAcceptVacation'])!.value;
      user.relatedUsers = this.editForm.get(['relatedUsers'])!.value;
    }
    user.approvalVerificationType = this.editForm.get(['approvalVerificationType'])!.value;
    user.approvalType = this.editForm.get(['approvalType'])!.value;
  }

  public onAuthorizationToAcceptVacationChange(event: any): void {
    if (event.value !== 'USER_GROUP') {
      this.editForm.get(['groups'])?.reset();
    }
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
  }
}
