CREATE TABLE market_indices (
    id              BIGSERIAL      PRIMARY KEY,
    symbol          VARCHAR(20)    NOT NULL,
    name            VARCHAR(100)   NOT NULL,
    date            DATE           NOT NULL,
    closing_price   NUMERIC(15, 4) NOT NULL,
    change_amount   NUMERIC(15, 4),
    change_rate     NUMERIC(7, 4),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_market_indices_symbol_date UNIQUE (symbol, date)
);

CREATE INDEX idx_market_indices_date ON market_indices(date);

COMMENT ON TABLE market_indices IS '시장 지표 (DXY, VIX, NASDAQ, USDKRW 등)';
COMMENT ON COLUMN market_indices.symbol IS 'Yahoo Finance ticker (예: DX-Y.NYB, ^VIX, ^IXIC, KRW=X)';
COMMENT ON COLUMN market_indices.closing_price IS '종가 (정밀도 4 — 환율 등 대응)';
COMMENT ON COLUMN market_indices.change_amount IS '전일 대비 변동량';
COMMENT ON COLUMN market_indices.change_rate IS '전일 대비 변동률 (%)';
