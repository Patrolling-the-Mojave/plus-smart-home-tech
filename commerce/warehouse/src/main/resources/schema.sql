CREATE TABLE IF NOT EXISTS product(
    product_id VARCHAR PRIMARY KEY,
    quantity INTEGER DEFAULT 0 CHECK(quantity >= 0),
    fragile BOOLEAN NOT NULL,
    width DOUBLE NOT NULL CHECK(width > 0),
    height DOUBLE NOT NULL CHECK(height > 0),
    depth DOUBLE NOT NULL CHECK(depth > 0),
    weight DOUBLE NOT NULL CHECK(weight > 0)
);