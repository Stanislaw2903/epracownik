import { MenuItem } from 'primeng/api';

export interface ICodeName {
  code: string | number;
  label: string;
}

export interface CustomMenuItem extends MenuItem {
  value: any;
}
