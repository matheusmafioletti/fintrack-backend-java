CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    description VARCHAR(200) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type VARCHAR(10) NOT NULL,
    date DATE NOT NULL,
    notes TEXT,
    recurring BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT chk_transaction_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT chk_transaction_amount CHECK (amount > 0)
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_transactions_date ON transactions(date);
CREATE INDEX idx_transactions_user_date ON transactions(user_id, date DESC);

COMMENT ON TABLE transactions IS 'Table for financial transactions';
COMMENT ON COLUMN transactions.amount IS 'Amount always positive, type defines if income or expense';
COMMENT ON COLUMN transactions.recurring IS 'Indicates if it is a recurring transaction';
