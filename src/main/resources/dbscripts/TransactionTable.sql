select * from transactions;
desc transactions;


INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (45000.00, 'Salary', '2025-05-25 14:00:15.123456', 'NOK', 'Monthly salary deposit', '2025-05-25 14:00:15.123456', 'INCOME', 1);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-1250.50, 'Groceries', '2025-05-25 16:30:22.456789', 'NOK', 'Weekly grocery shopping at Rema 1000', '2025-05-25 16:30:22.456789', 'EXPENSE', 1);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (2000.00, 'Emergency Fund', '2025-05-25 17:15:30.789123', 'NOK', 'Monthly savings contribution', '2025-05-25 17:15:30.789123', 'SAVINGS', 2);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-850.00, 'Transportation', '2025-05-24 08:45:18.234567', 'NOK', 'Monthly train pass', '2025-05-24 08:45:18.234567', 'EXPENSE', 3);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (12500.00, 'Freelance', '2025-05-24 11:20:45.567890', 'NOK', 'Web development project payment', '2025-05-24 11:20:45.567890', 'INCOME', 3);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-12000.00, 'Rent', '2025-05-23 09:00:00.000000', 'NOK', 'Monthly apartment rent', '2025-05-23 09:00:00.000000', 'EXPENSE', 4);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (5000.00, 'Investment', '2025-05-23 14:35:22.345678', 'NOK', 'Stock market investment', '2025-05-23 14:35:22.345678', 'SAVINGS', 4);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-1450.00, 'Utilities', '2025-05-22 15:20:30.678901', 'NOK', 'Electricity and internet bill', '2025-05-22 15:20:30.678901', 'EXPENSE', 5);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (8750.00, 'Bonus', '2025-05-22 10:15:45.901234', 'NOK', 'Performance bonus', '2025-05-22 10:15:45.901234', 'INCOME', 5);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (3500.00, 'Account Transfer', '2025-05-21 16:30:22.567890', 'NOK', 'Transfer from checking to savings', '2025-05-21 16:30:22.567890', 'TRANSFER', 6);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-1680.25, 'Shopping', '2025-05-21 13:45:18.234567', 'NOK', 'Clothing and accessories', '2025-05-21 13:45:18.234567', 'EXPENSE', 7);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-320.00, 'Entertainment', '2025-05-20 19:45:30.890123', 'NOK', 'Movie tickets and dinner', '2025-05-20 19:45:30.890123', 'EXPENSE', 8);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (38000.00, 'Salary', '2025-05-20 09:00:15.123456', 'NOK', 'Monthly salary deposit', '2025-05-20 09:00:15.123456', 'INCOME', 9);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-1200.00, 'Healthcare', '2025-05-19 11:20:45.456789', 'NOK', 'Dental appointment', '2025-05-19 11:20:45.456789', 'EXPENSE', 10);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (7500.00, 'Retirement', '2025-05-19 14:35:22.789123', 'NOK', 'Pension fund contribution', '2025-05-19 14:35:22.789123', 'SAVINGS', 11);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-680.50, 'Dining', '2025-05-18 18:25:30.012345', 'NOK', 'Restaurant dinner with friends', '2025-05-18 18:25:30.012345', 'EXPENSE', 12);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (15000.00, 'Consulting', '2025-05-18 12:45:18.345678', 'NOK', 'Business consulting fee', '2025-05-18 12:45:18.345678', 'INCOME', 13);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (2500.00, 'Internal Transfer', '2025-05-17 15:30:22.901234', 'NOK', 'Transfer between accounts', '2025-05-17 15:30:22.901234', 'TRANSFER', 14);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-2500.00, 'Insurance', '2025-05-17 10:15:45.678901', 'NOK', 'Car insurance premium', '2025-05-17 10:15:45.678901', 'EXPENSE', 15);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-3200.00, 'Education', '2025-05-16 09:45:18.234567', 'NOK', 'Online course enrollment', '2025-05-16 09:45:18.234567', 'EXPENSE', 16);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (4500.00, 'Emergency Fund', '2025-05-16 13:20:30.567890', 'NOK', 'Emergency savings contribution', '2025-05-16 13:20:30.567890', 'SAVINGS', 17);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-890.75, 'Fuel', '2025-05-15 07:35:45.890123', 'NOK', 'Gasoline for car', '2025-05-15 07:35:45.890123', 'EXPENSE', 18);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (22000.00, 'Part-time Job', '2025-05-15 17:00:15.123456', 'NOK', 'Part-time income', '2025-05-15 17:00:15.123456', 'INCOME', 19);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (-1450.00, 'Subscription', '2025-05-14 12:25:30.456789', 'NOK', 'Software and streaming subscriptions', '2025-05-14 12:25:30.456789', 'EXPENSE', 20);

INSERT INTO transactions (amount, category, created_at, currency, description, transaction_date, transaction_type, account_id)
VALUES (6750.00, 'Investment', '2025-05-14 16:40:45.789123', 'NOK', 'Index fund investment', '2025-05-14 16:40:45.789123', 'SAVINGS', 21);



