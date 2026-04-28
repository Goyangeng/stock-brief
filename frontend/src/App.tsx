import { useEffect, useMemo, useState } from 'react';
import './App.css';
import {
  createStock,
  deleteStock,
  fetchPrices,
  fetchStocks,
  type DailyPrice,
  type Stock,
  type StockCreateRequest,
} from './api/stocks';
import { CandlestickChart } from './components/CandlestickChart';
import { MarketIndicesBar } from './components/MarketIndicesBar';

const PAGE_SIZE = 20;

function App() {
  const [stocks, setStocks] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<StockCreateRequest>({
    ticker: '',
    name: '',
    market: '',
    memo: '',
  });
  const [selectedStockId, setSelectedStockId] = useState<number | null>(null);
  const [prices, setPrices] = useState<DailyPrice[]>([]);
  const [pricesLoading, setPricesLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);

  const loadStocks = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchStocks();
      setStocks(data);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    loadStocks();
  }, []);

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return stocks;
    return stocks.filter(
      (s) =>
        s.ticker.toLowerCase().includes(q) ||
        s.name.toLowerCase().includes(q) ||
        s.market.toLowerCase().includes(q),
    );
  }, [stocks, search]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const currentPage = Math.min(page, totalPages);
  const pageStart = (currentPage - 1) * PAGE_SIZE;
  const paged = filtered.slice(pageStart, pageStart + PAGE_SIZE);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createStock({
        ticker: form.ticker,
        name: form.name,
        market: form.market,
        memo: form.memo || undefined,
      });
      setForm({ ticker: '', name: '', market: '', memo: '' });
      await loadStocks();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Unknown error');
    }
  };

  const handleDelete = async (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    if (!window.confirm('정말 삭제하시겠어요?')) return;
    try {
      await deleteStock(id);
      if (selectedStockId === id) {
        setSelectedStockId(null);
        setPrices([]);
      }
      await loadStocks();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const handleSelectStock = async (id: number) => {
    if (selectedStockId === id) {
      setSelectedStockId(null);
      setPrices([]);
      return;
    }
    setSelectedStockId(id);
    setPricesLoading(true);
    try {
      const data = await fetchPrices(id);
      setPrices(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setPricesLoading(false);
    }
  };

  return (
    <div className="app">
      <h1>📊 stock-brief</h1>

      <MarketIndicesBar />

      <section className="form-section">
        <h2>관심 종목 등록</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="ticker (예: AAPL)"
            value={form.ticker}
            onChange={(e) => setForm({ ...form, ticker: e.target.value })}
            required
          />
          <input
            type="text"
            placeholder="이름 (예: Apple Inc.)"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
          <input
            type="text"
            placeholder="시장 (예: NASDAQ)"
            value={form.market}
            onChange={(e) => setForm({ ...form, market: e.target.value })}
            required
          />
          <input
            type="text"
            placeholder="메모 (선택)"
            value={form.memo ?? ''}
            onChange={(e) => setForm({ ...form, memo: e.target.value })}
          />
          <button type="submit">등록</button>
        </form>
      </section>

      <section className="list-section">
        <div className="list-header">
          <h2>등록된 종목 ({filtered.length}개)</h2>
          <input
            type="text"
            className="search-input"
            placeholder="검색 (ticker/name/market)"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(1);
            }}
          />
        </div>
        {loading && <p>로딩 중...</p>}
        {error && <p className="error">에러: {error}</p>}
        {!loading && filtered.length === 0 && <p>일치하는 종목이 없습니다.</p>}
        <ul className="stock-list">
          {paged.map((stock) => {
            const isSelected = selectedStockId === stock.id;
            return (
              <li key={stock.id} className={`stock-item ${isSelected ? 'selected' : ''}`}>
                <div className="stock-row" onClick={() => handleSelectStock(stock.id)}>
                  <div>
                    <strong>{stock.ticker}</strong>
                    <span className="market">({stock.market})</span>
                    <span className="name">{stock.name}</span>
                    {stock.memo && <span className="memo">— {stock.memo}</span>}
                  </div>
                  <button onClick={(e) => handleDelete(e, stock.id)} className="delete-btn">
                    삭제
                  </button>
                </div>
                {isSelected && (
                  <div className="chart-container">
                    {pricesLoading ? <p>차트 로딩 중...</p> : <CandlestickChart prices={prices} />}
                  </div>
                )}
              </li>
            );
          })}
        </ul>
        {totalPages > 1 && (
          <div className="pagination">
            <button onClick={() => setPage((p) => Math.max(1, p - 1))} disabled={currentPage === 1}>
              이전
            </button>
            <span>
              {currentPage} / {totalPages}
            </span>
            <button
              onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              disabled={currentPage === totalPages}
            >
              다음
            </button>
          </div>
        )}
      </section>
    </div>
  );
}

export default App;
