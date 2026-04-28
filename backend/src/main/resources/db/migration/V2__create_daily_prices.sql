CREATE TABLE daily_prices (
    id              BIGSERIAL      PRIMARY KEY,
    stock_id        BIGINT         NOT NULL REFERENCES stocks(id),
    date            DATE           NOT NULL,
    opening_price   NUMERIC(15, 2) NOT NULL,
    closing_price   NUMERIC(15, 2) NOT NULL,
    high_price      NUMERIC(15, 2) NOT NULL,
    low_price       NUMERIC(15, 2) NOT NULL,
    change_rate     NUMERIC(7, 4),
    volume          BIGINT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_daily_prices_stock_date UNIQUE (stock_id, date)
);

CREATE INDEX idx_daily_prices_date ON daily_prices(date);

COMMENT ON TABLE daily_prices IS '특정 종목의 특정 날짜 가격 (Yahoo Finance 자동 수집, 불변)';
COMMENT ON COLUMN daily_prices.stock_id IS 'FK to stocks(id)';
COMMENT ON COLUMN daily_prices.change_rate IS '전일 대비 변동률(%)';
COMMENT ON COLUMN daily_prices.volume IS '거래량';
