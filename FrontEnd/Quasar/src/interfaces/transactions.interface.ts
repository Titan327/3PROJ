import {Group} from "src/interfaces/group.interface";

export interface Transaction {

id : number,
group: Group,
label: string,
total_amount: number,
date: Date,
receipt: string,
sender_id: number,
category_id: number,

}
