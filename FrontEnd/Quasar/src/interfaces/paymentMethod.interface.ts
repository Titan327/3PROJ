export interface IPaymentMethod {
  id?: string
  type?: string;
  value?: string;
  userId?: number;
}

export function DefaultPaymentMethod(): IPaymentMethod {
  return {
    id: "aa",
    type: "Paypal",
    value: "test",
  };
}
export interface Rib
{
  "type": string,
  "name": string,
  "surname": string,
  "bank_name": string,
  "bank_number"?: number,
  "box_code"?:  number,
  "account_number"?: number,
  "RIB_key"?:  number,
  "IBAN": string,

}
export function DefaultRib(): Rib {
  return {
    "type": "RIB",
    "name": "",
    "surname": "",
    "bank_name": "",
    "IBAN": "",
  };
}
