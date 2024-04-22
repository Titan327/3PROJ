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
