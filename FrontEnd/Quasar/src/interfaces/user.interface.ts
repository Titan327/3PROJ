export interface IUser {
  id?: number
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  birthdate: string;
}

export function DefaultUser(): IUser {
  return {
    id: 0,
    username: '',
    email: '',
    birthdate: '',
    firstName: '',
    lastName: '',
  };
}
