export interface IUser {
  id?: number
  username?: string;
  email?: string;
  firstname?: string;
  lastname?: string;
  birth_date?: Date;
  token?: string;
  profile_picture?: [string];
}

export function DefaultUser(): IUser {
  return {
    id: 0,
    username: '',
    email: '',
    birth_date: new Date(),
    firstname: '',
    lastname: '',
    token: '',
  };
}
