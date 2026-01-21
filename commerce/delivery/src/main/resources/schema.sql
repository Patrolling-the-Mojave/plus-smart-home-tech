CREATE TABLE IF NOT EXISTS delivery(
    id VARCHAR PRIMARY KEY,
    delivery_cost DOUBLE CHECK(delivery_cost > 0),
    order_id VARCHAR NOT NULL,
    total_weight DOUBLE CHECK(total_weight > 0),
    total_volume DOUBLE CHECK(total_volume > 0),
    fragile BOOLEAN,
    status VARCHAR CHECK (status IN ('CREATED', 'IN_PROGRESS', 'DELIVERED', 'FAILED', 'CANCELLED')),


    origin_country VARCHAR,
    origin_city VARCHAR,
    origin_street VARCHAR,
    origin_house VARCHAR,
    origin_flat VARCHAR,

    destination_country VARCHAR,
    destination_city VARCHAR,
    destination_street VARCHAR,
    destination_house VARCHAR,
    destination_flat VARCHAR 
);

CREATE INDEX idx_delivery_order_id ON delivery(order_id);