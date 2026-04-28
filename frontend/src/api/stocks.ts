const API_BASE_URL = 'http://localhost:8080/api';

export interface Stock {
  id: number;
  ticker: string;
  name: string;
  market: string;
  memo: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface StockCreateRequest {
  ticker: string;
  name: string;
  market: string;
  memo?: string;
}

export interface DailyPrice {
  id: number;
  stockId: number;
  ticker: string;
  date: string; // ISO LocalDate (YYYY-MM-DD)
  openingPrice: number;
  closingPrice: number;
  highPrice: number;
  lowPrice: number;
  changeRate: number | null;
  volume: number | null;
  createdAt: string; // ISO Instant
}

export async function fetchStocks(): Promise<Stock[]> {
  const response = await fetch(`${API_BASE_URL}/stocks`);
  if (!response.ok) {
    throw new Error(`Failed to fetch stocks: ${response.status}`);
  }
  return response.json();
}

export async function createStock(request: StockCreateRequest): Promise<Stock> {
  const response = await fetch(`${API_BASE_URL}/stocks`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  if (!response.ok) {
    throw new Error(`Failed to create stock: ${response.status}`);
  }
  return response.json();
}

export async function deleteStock(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/stocks/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    throw new Error(`Failed to delete stock: ${response.status}`);
  }
}

export async function fetchPrices(stockId: number): Promise<DailyPrice[]> {
  const response = await fetch(`${API_BASE_URL}/stocks/${stockId}/prices`);
  if (!response.ok) {
    throw new Error(`Failed to fetch prices: ${response.status}`);
  }
  return response.json();
}
