CREATE TABLE product_codes (
    id SERIAL PRIMARY KEY,
    code_type VARCHAR(50) CHECK((CHAR_LENGTH(code_type) between 5 and 50)),
    code_value TEXT NOT NULL,
);

CREATE TABLE scan_info (
    id SERIAL PRIMARY KEY,
    scan_datetime TIMESTAMP NOT NULL,
    product_code_id INTEGER NOT NULL,
    success BOOLEAN,
    CONSTRAINT fk_product_code FOREIGN KEY (product_code_id) REFERENCES product_codes(id) ON DELETE CASCADE
);
