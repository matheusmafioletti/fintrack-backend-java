CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    period VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_budgets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_category_period UNIQUE (user_id, category_id, period),
    CONSTRAINT chk_budget_period CHECK (period IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
    CONSTRAINT chk_budget_amount CHECK (amount > 0),
    CONSTRAINT chk_budget_dates CHECK (end_date IS NULL OR end_date > start_date)
);

CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_category_id ON budgets(category_id);
CREATE INDEX idx_budgets_period ON budgets(period);

COMMENT ON TABLE budgets IS 'Table for budgets by category';
COMMENT ON COLUMN budgets.period IS 'Budget period: WEEKLY, MONTHLY or YEARLY';
COMMENT ON COLUMN budgets.end_date IS 'Budget end date (NULL indicates no end date)';
