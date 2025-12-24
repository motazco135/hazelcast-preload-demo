
-- Create sequence for customer ID
CREATE SEQUENCE IF NOT EXISTS customer_id_seq
START WITH 1
INCREMENT BY 100
cache 100 ;

CREATE TABLE IF NOT EXISTS t_customer (
    customer_id BIGINT PRIMARY KEY DEFAULT nextval('customer_id_seq'),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    mobile VARCHAR(255),
    address VARCHAR(255),
    id_type INTEGER NOT NULL,
    id_number VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);



-- Comment on enum mapping: 1 = IQAMA, 2 = NIN
COMMENT ON COLUMN t_customer.id_type IS 'ID Type: 2=IQAMA, 1=NIN';
