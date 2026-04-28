import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { type DailyPrice } from '../api/stocks';

interface Props {
  prices: DailyPrice[];
}

export function PriceChart({ prices }: Props) {
  if (prices.length === 0) {
    return (
      <p className="chart-empty">
        가격 데이터가 없습니다. 매일 07:00에 자동 수집됩니다.
        <br />
        수동 수집은 백엔드의 <code>POST /api/prices/collect/{'{stockId}'}</code>
      </p>
    );
  }

  // API 응답이 날짜 내림차순(desc) → 차트는 오름차순으로 표시
  const data = [...prices].reverse().map((p) => ({
    date: p.date,
    close: p.closingPrice,
  }));

  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#333" />
        <XAxis dataKey="date" stroke="#888" />
        <YAxis domain={['auto', 'auto']} stroke="#888" />
        <Tooltip
          contentStyle={{ background: '#222', border: '1px solid #444' }}
          labelStyle={{ color: '#aaa' }}
        />
        <Line
          type="monotone"
          dataKey="close"
          stroke="#646cff"
          strokeWidth={2}
          dot={{ r: 3 }}
          activeDot={{ r: 5 }}
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
