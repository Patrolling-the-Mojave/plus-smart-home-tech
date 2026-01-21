CREATE TABLE IF NOT EXISTS payment(
    id VARCHAR PRIMARY KEY,
    order_id VARCHAR NOT NULL,
    product_cost DOUBLE,
    total_cost DOUBLE,
    fee_total DOUBLE,
    status VARCHAR NOT NULL CHECK(status IN ('PENDING', 'SUCCESS', 'FAILED'))
);