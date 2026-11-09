export interface IControlReadFiles {
  fileType?: string;
  viewed?: number;
  confirmed?: number;
  firstName?: string;
  lastName?: string;
  userId?: number;
  fileName?: string;
}

export class ControlReadFiles implements IControlReadFiles {
  constructor(
    public fileType?: string,
    public viewed?: number,
    public confirmed?: number,
    public firstName?: string,
    public lastName?: string,
    public userId?: number,
    public fileName?: string
  ) {}
}
