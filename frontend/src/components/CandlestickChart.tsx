import { useEffect, useRef } from 'react';
import { CandlestickSeries, createChart, type IChartApi } from 'lightweight-charts';
import { type DailyPrice } from '../api/stocks';

interface Props {
  prices: DailyPrice[];
}

export function CandlestickChart({ prices }: Props) {
  const containerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<IChartApi | null>(null);

  useEffect(() => {
    if (!containerRef.current || prices.length === 0) return;

    const chart = createChart(containerRef.current, {
      width: containerRef.current.clientWidth,
      height: 320,
      layout: {
        background: { color: '#0f0f1a' },
        textColor: '#aaa',
      },
      grid: {
        vertLines: { color: '#222' },
        horzLines: { color: '#222' },
      },
      timeScale: {
        timeVisible: false,
        secondsVisible: false,
      },
    });

    chartRef.current = chart;

    const candleSeries = chart.addSeries(CandlestickSeries, {
      upColor: '#26a69a',
      downColor: '#ef5350',
      borderVisible: false,
      wickUpColor: '#26a69a',
      wickDownColor: '#ef5350',
    });

    // API는 desc → 차트는 asc 시간순
    const data = [...prices].reverse().map((p) => ({
      time: p.date,
      open: p.openingPrice,
      high: p.highPrice,
      low: p.lowPrice,
      close: p.closingPrice,
    }));

    candleSeries.setData(data);
    chart.timeScale().fitContent();

    const handleResize = () => {
      if (containerRef.current) {
        chart.applyOptions({ width: containerRef.current.clientWidth });
      }
    };
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
      chart.remove();
      chartRef.current = null;
    };
  }, [prices]);

  if (prices.length === 0) {
    return (
      <p className="chart-empty">
        가격 데이터가 없습니다. <code>POST /api/prices/backfill/{'{stockId}'}</code>로 채우세요.
      </p>
    );
  }

  return <div ref={containerRef} style={{ width: '100%', height: 320 }} />;
}
