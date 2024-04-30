export interface UserStatistics {
  statistic: string;
  average?: string;
  transactions?: number;
  amountByCategories?: Record<string, number>;
  numberByCategories?: Record<string, number>;
  averageByCategories?: Record<string, string>;
  amountByMonth?: Record<string, number>;
  numberByMonth?: Record<string, number>;
}
