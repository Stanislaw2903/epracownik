import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UserGroupStateService {
  private group: any = null;

  setGroup(group: any): void {
    this.group = group;
  }

  getGroup(): any {
    return this.group;
  }

  clearGroup(): void {
    this.group = null;
  }
}
