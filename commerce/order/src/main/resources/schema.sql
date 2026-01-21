CREATE TABLE IF NOT EXISTS orders(
    id VARCHAR PRIMARY KEY,
    shopping_cart_id VARCHAR NOT NULL UNIQUE,
    payment_id VARCHAR NOT NULL UNIQUE,
    delivery_id VARCHAR NOT NULL UNIQUE,
    state VARCHAR NOT NULL CHECK(state IN ('NEW', 'ON_PAYMENT', 'ON_DELIVERY', 'DONE', 'DELIVERED', 'ASSEMBLED', 'PAID', 'COMPLETED', 'DELIVERY_FAILED', 'ASSEMBLY_FAILED', 'PAYMENT_FAILED', 'PRODUCT_RETURNED', 'CANCELED')),
    delivery_weight DOUBLE,
    delivery_volume DOUBLE,
    fragile BOOLEAN,
    total_price DOUBLE,
    delivery_price DOUBLE,
    product_price DOUBLE
);

CREATE TABLE IF NOT EXISTS order_products(
    product_id VARCHAR NOT NULL,
    order_id VARCHAR NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY(product_id, order_id)
);