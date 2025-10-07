CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(10) NOT NULL,
    color VARCHAR(7) DEFAULT '#808080',
    icon VARCHAR(50) DEFAULT 'default',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_category_name UNIQUE (user_id, name),
    CONSTRAINT chk_category_type CHECK (type IN ('INCOME', 'EXPENSE'))
);

CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_type ON categories(type);

COMMENT ON TABLE categories IS 'Table for transaction categories';
COMMENT ON COLUMN categories.type IS 'Category type: INCOME (revenue) or EXPENSE (expense)';
COMMENT ON COLUMN categories.color IS 'Color in hexadecimal format (#RRGGBB)';
