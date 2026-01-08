CREATE TABLE IF NOT EXISTS product (
    id VARCHAR PRIMARY KEY,
    product_name VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(200) NOT NULL,
    quantity_state VARCHAR NOT NULL CHECK(quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    product_category VARCHAR NOT NULL CHECK(product_category IN ('LIGHTING', 'CONTROL', 'SENSORS')),
    product_state VARCHAR NOT NULL CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    price DECIMAL NOT NULL CHECK(price > 0)
);
CREATE INDEX idx_product_name ON product(product_name);
CREATE INDEX idx_product_price ON product(price);

