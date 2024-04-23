import {api} from "boot/axios";

export const formatDate = (dateString) => {
  const date = new Date(dateString);
  const day = date.getDate();
  const month = date.getMonth() + 1;
  const year = date.getFullYear();
  return `${day < 10 ? '0' : ''}${day}/${month < 10 ? '0' : ''}${month}/${year}`;
};

export function formatNumber(num) {
  const roundedNum = Math.round((num + Number.EPSILON) * 100) / 100;
  const formattedNum = roundedNum.toFixed(2);
  return formattedNum;
}

export const redirectToPaypal = (username:string) => {
  window.open(`https://paypal.me/${username}`, '_blank');
};

export function convertIBAN(iban:string){
  const firstFour = iban.substring(0, 4);
  const lastFour = iban.substring(iban.length - 4);
  const middleStars = '*'.repeat(iban.length - 8);
  const maskedIBAN = firstFour + middleStars + lastFour;
  return maskedIBAN.match(/.{1,4}/g).join(' ')
}

export function redirectBankWebSite(link:string){
  window.open(link, '_blank');
}
