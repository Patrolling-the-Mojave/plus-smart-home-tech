CREATE TABLE IF NOT EXISTS shopping_cart(
    id VARCHAR(40) PRIMARY KEY,
    status VARCHAR NOT NULL CHECK(status IN ('ACTIVE', 'DEACTIVATE'))
);

CREATE TABLE IF NOT EXISTS cart_product(
    cart_id VARCHAR FOREIGN KEY REFERENCES shopping_cart(id),
    product_id VARCHAR NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY(cart_id, product_id)
);

CREATE INDEX idx_cart_product_cart_id ON cart_product(cart_id);