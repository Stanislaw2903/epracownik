export class Account {
  constructor(
    public activated: boolean,
    public authorities: string[],
    public email: string,
    public id: number,
    public firstName: string,
    public langKey: string,
    public lastName: string,
    public login: string,
    public imageUrl: string,
    public phoneNumber?: string
  ) {}
}
