import { useEffect, useState } from 'react';
import { fetchMarketIndices, type MarketIndex } from '../api/market';

export function MarketIndicesBar() {
  const [indices, setIndices] = useState<MarketIndex[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchMarketIndices()
      .then(setIndices)
      .catch((e) => setError(e instanceof Error ? e.message : 'Unknown error'));
  }, []);

  if (error) {
    return <div className="market-bar market-bar-error">시장 지표 로딩 실패: {error}</div>;
  }
  if (indices.length === 0) {
    return <div className="market-bar">시장 지표 로딩 중...</div>;
  }

  return (
    <div className="market-bar">
      {indices.map((idx) => {
        const isPositive = (idx.changeRate ?? 0) >= 0;
        return (
          <div key={idx.symbol} className="market-card">
            <div className="market-name">{idx.name}</div>
            <div className="market-price">
              {idx.closingPrice.toLocaleString(undefined, { maximumFractionDigits: 2 })}
            </div>
            {idx.changeRate != null && (
              <div className={`market-change ${isPositive ? 'positive' : 'negative'}`}>
                {isPositive ? '+' : ''}
                {idx.changeRate.toFixed(2)}%
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}
