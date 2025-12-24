CREATE TABLE t_ods_landing (
  customer_id       BIGINT PRIMARY KEY,
  bundle_json       JSONB NOT NULL,
  last_updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ods_landing_last_updated ON t_ods_landing(last_updated_at);