-- Sample data for testing FinTrack API
-- This script creates a demo user and sample transactions

-- Note: The user password is 'password123' (will be encrypted by the application)
-- This script should be run after the application has created a user via /api/auth/register

-- Example: First register a user via API, then use their ID here

-- Sample transactions for user_id = 1 (replace with actual user ID)
-- Assuming categories were created automatically on registration

-- Income transactions
INSERT INTO transactions (user_id, category_id, description, amount, type, date, notes, recurring) VALUES
(1, 8, 'Monthly Salary', 5000.00, 'INCOME', '2025-10-01', 'Regular monthly income', false),
(1, 9, 'Freelance Project', 1500.00, 'INCOME', '2025-10-05', 'Web development project', false),
(1, 8, 'Monthly Salary', 5000.00, 'INCOME', '2025-09-01', 'Previous month salary', false);

-- Expense transactions
INSERT INTO transactions (user_id, category_id, description, amount, type, date, notes, recurring) VALUES
(1, 1, 'Supermarket Shopping', 250.50, 'EXPENSE', '2025-10-02', 'Weekly groceries', false),
(1, 2, 'Gas Station', 150.00, 'EXPENSE', '2025-10-03', 'Car fuel', false),
(1, 1, 'Restaurant Lunch', 45.00, 'EXPENSE', '2025-10-04', 'Business lunch', false),
(1, 3, 'Rent Payment', 1200.00, 'EXPENSE', '2025-10-01', 'Monthly rent', true),
(1, 4, 'Medical Appointment', 180.00, 'EXPENSE', '2025-10-06', 'Doctor consultation', false),
(1, 2, 'Uber Rides', 85.50, 'EXPENSE', '2025-10-05', 'Transportation this week', false),
(1, 1, 'Coffee Shop', 25.00, 'EXPENSE', '2025-10-07', 'Morning coffee', false),
(1, 6, 'Movie Theater', 60.00, 'EXPENSE', '2025-10-07', 'Weekend entertainment', false),
(1, 1, 'Groceries', 180.00, 'EXPENSE', '2025-09-15', 'Last month shopping', false),
(1, 2, 'Gas', 140.00, 'EXPENSE', '2025-09-20', 'Last month fuel', false);

-- Sample budgets for user_id = 1
INSERT INTO budgets (user_id, category_id, amount, period, start_date, end_date) VALUES
(1, 1, 1000.00, 'MONTHLY', '2025-10-01', NULL),
(1, 2, 500.00, 'MONTHLY', '2025-10-01', NULL),
(1, 3, 1200.00, 'MONTHLY', '2025-10-01', NULL),
(1, 6, 200.00, 'MONTHLY', '2025-10-01', NULL);

-- To use this script:
-- 1. Register a user via POST /api/auth/register
-- 2. Note the user ID from the response
-- 3. Replace all user_id = 1 with your actual user ID
-- 4. Replace category_id values if they differ
-- 5. Run: psql -U fintrack_user -d fintrack -f scripts/seed-data.sql
