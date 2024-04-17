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
