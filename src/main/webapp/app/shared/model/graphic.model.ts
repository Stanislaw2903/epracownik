export interface IGraphicDayCell {
  type: string; // WORKING / WEEKEND / HOLIDAY / ABSENCE
  code?: string; // tylko ABSENCE (np. UW)
  category?: string; // tylko ABSENCE (A/B/C/D)
  absKod?: string; // tylko ABSENCE - oficjalny kod (U/K/J...)
}

export interface IGraphicTableItem {
  firstName?: string;
  lastName?: string;
  days?: { [date: string]: IGraphicDayCell };
}

export interface IGraphicDayColumn {
  key: string;
  label: string;
}

export class Graphic implements IGraphicTableItem {
  constructor(public firstName?: string, public lastName?: string, public days: { [date: string]: IGraphicDayCell } = {}) {}
}
