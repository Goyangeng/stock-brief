import { useEffect, useState } from 'react';
import './App.css';
import {
  createStock,
  deleteStock,
  fetchStocks,
  type Stock,
  type StockCreateRequest,
} from './api/stocks';

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
    loadStocks();
  }, []);

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

  const handleDelete = async (id: number) => {
    if (!window.confirm('정말 삭제하시겠어요?')) return;
    try {
      await deleteStock(id);
      await loadStocks();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Unknown error');
    }
  };

  return (
    <div className="app">
      <h1>📊 stock-brief</h1>

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
        <h2>등록된 종목 ({stocks.length}개)</h2>
        {loading && <p>로딩 중...</p>}
        {error && <p className="error">에러: {error}</p>}
        {!loading && stocks.length === 0 && <p>등록된 종목이 없습니다.</p>}
        <ul className="stock-list">
          {stocks.map((stock) => (
            <li key={stock.id} className="stock-item">
              <div>
                <strong>{stock.ticker}</strong>
                <span className="market">({stock.market})</span>
                <span className="name">{stock.name}</span>
                {stock.memo && <span className="memo">— {stock.memo}</span>}
              </div>
              <button onClick={() => handleDelete(stock.id)} className="delete-btn">
                삭제
              </button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}

export default App;
