CREATE TABLE stocks (
    id          BIGSERIAL     PRIMARY KEY,
    ticker      VARCHAR(20)   NOT NULL UNIQUE,
    name        VARCHAR(100)  NOT NULL,
    market      VARCHAR(20)   NOT NULL,
    memo        TEXT,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE stocks IS '사용자가 관심 등록한 종목 (개별 주식 + ETF)';
COMMENT ON COLUMN stocks.ticker IS '종목 코드 (예: AAPL, 005930.KS)';
COMMENT ON COLUMN stocks.market IS 'NASDAQ, KOSPI, KOSDAQ, NYSE 등';
COMMENT ON COLUMN stocks.memo IS '사용자 메모 (장기보유 등)';
