
-- Create sequence for customer ID
CREATE SEQUENCE IF NOT EXISTS account_id_seq
START WITH 1
INCREMENT BY 100;

CREATE TABLE t_account (
    account_id          BIGINT  PRIMARY KEY DEFAULT nextval('account_id_seq'),
    customer_id         BIGINT  NOT NULL,

    iban                VARCHAR(34),
    account_type        INTEGER   NOT NULL,   -- CURRENT, SAVINGS
    currency            VARCHAR(3)    NOT NULL,   -- SAR, USD
    account_status      INTEGER   NOT NULL,   -- ACTIVE, DORMANT, CLOSED

    available_balance   NUMERIC(18,2) NOT NULL DEFAULT 0,
    ledger_balance      NUMERIC(18,2) NOT NULL DEFAULT 0,

    is_primary           BOOLEAN       DEFAULT FALSE,
    opened_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comment on enum mapping
COMMENT ON COLUMN t_account.account_type IS 'account type: 1=CURRENT, 2=SAVINGS';
COMMENT ON COLUMN t_account.account_status IS 'account STATUS : 1=ACTIVE, 2=DORMANT, 3=CLOSED';