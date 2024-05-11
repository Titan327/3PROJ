import {IUser} from "src/interfaces/user.interface";

export interface Group{
  id : number,
  name: string,
  description: string,
  picture?: string,
  ownerId?: number,
  updatedAt?: Date,
  activeUsersCount?: number,
  Users?: IUser[],
}

export function DefaultGroup(): Group {
  return {
    id: 0,
    name: '',
    description: '',
  };
}
