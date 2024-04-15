import {Group} from "src/interfaces/group.interface";

export interface Transaction {
  id: number,
  userId: number,
  transactionId: number,
  amount: number,
  createdAt: Date,
  updatedAt: Date,
  Transaction: {
    id : number,
    group: Group,
    label: string,
    total_amount: number,
    date: Date,
    receipt: string,
    sender_id: number,
    category_id: number,
  }
}

export interface TransactionCreated {
  "groupId": number,
  "label": string,
  "total_amount": number,
  "date":Date,
  "receipt": string,
  "senderId": number,
  "categoryId": number,
  "details"?: [
    {
    "detail0":{
      "userId": number,
      "amount": number
    } }
  ]
}

export function DefaultTransactionCreated():TransactionCreated{
return {
  "groupId": 0,
  "label": '',
  "total_amount": 0,
  "date": new Date(),
  "receipt": '',
  "senderId": 0,
  "categoryId": 1,
}
}
