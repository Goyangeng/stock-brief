const API_BASE_URL = 'http://localhost:8080/api';

export interface MarketIndex {
  id: number;
  symbol: string;
  name: string;
  date: string;
  closingPrice: number;
  changeAmount: number | null;
  changeRate: number | null;
  createdAt: string;
}

export async function fetchMarketIndices(): Promise<MarketIndex[]> {
  const response = await fetch(`${API_BASE_URL}/market-indices/latest`);
  if (!response.ok) {
    throw new Error(`Failed to fetch market indices: ${response.status}`);
  }
  return response.json();
}
