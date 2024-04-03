import {IUser} from "src/interfaces/user.interface";

export interface IMessage {
  text: string;
  stamp: Date;
  user:Partial<IUser>;
}
