export interface VacationDetails {
  id: number;
  month: Date;
  numberDayOfVacation: number;
  dayOfVacation: Date[];
}

export interface IVacationApplication {
  fullDay?: boolean | null;
  vacationType?: any;
  dateFrom?: Date;
  dateTo?: Date;
  comment?: string;
  used?: number;
}

export interface IVacationFullDayDict {
  name: string;
  value: boolean;
}
