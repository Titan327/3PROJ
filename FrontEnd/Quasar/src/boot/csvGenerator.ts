import {Transaction} from "src/interfaces/transactions.interface";
import {formatDate} from "stores/globalFunctionsStore";

export function generateCSV(transactions: Transaction[]): string {
  const header = [
    'ID', 'Titre', 'Montant total', 'Date','URL Ticket de caisse','ID du créateur', 'ID de la catégorie'
  ];

  const rows = transactions.map(transaction => [
    transaction.id,
    transaction.label,
    transaction.total_amount,
    formatDate(transaction.date),
    transaction.receipt,
    transaction.senderId,
    transaction.categoryId,
  ]);

  const csvContent = [header, ...rows].map(e => e.join(",")).join("\n");
  return csvContent;
}

export function downloadCSV(csvContent: string, fileName: string): void {
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement("a");
  const url = URL.createObjectURL(blob);
  link.setAttribute("href", url);
  link.setAttribute("download", fileName);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

