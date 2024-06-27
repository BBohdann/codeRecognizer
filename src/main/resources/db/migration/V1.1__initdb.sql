-- Створення таблиці product_codes
CREATE TABLE product_codes (
    id SERIAL PRIMARY KEY,
    code_type VARCHAR(50) CHECK (CHAR_LENGTH(code_type) BETWEEN 4 AND 50),
    code_value TEXT NOT NULL,
    file_name VARCHAR(255) CHECK (CHAR_LENGTH(file_name) BETWEEN 2 AND 255)
);

-- Створення таблиці scan_info
CREATE TABLE scan_info (
    id SERIAL PRIMARY KEY,
    scan_datetime TIMESTAMP,
    product_code_id BIGINT,
    success BOOLEAN,
    code_value_type VARCHAR(15),
    CONSTRAINT fk_product_code FOREIGN KEY (product_code_id) REFERENCES product_codes(id) ON DELETE CASCADE
);




