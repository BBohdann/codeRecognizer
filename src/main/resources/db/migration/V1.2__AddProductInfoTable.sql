-- Створення нової таблиці для зберігання інформації про продукт
CREATE TABLE product_info (
    id SERIAL PRIMARY KEY,
    code_value VARCHAR(255) NOT NULL,
    product_data JSONB NOT NULL,
    source VARCHAR(255) NOT NULL
);