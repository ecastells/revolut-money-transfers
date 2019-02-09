INSERT INTO currency
VALUES (1, 'ARG'), (2, 'EUR'), (3, 'USD'), (4, 'GBP');

INSERT INTO account (owner, balance, pendingTransfer, currency_id)
VALUES
('Emiliano Castells', 2000.75, 0, 2),
('Grecia Zoppi', 5000, 0, 1);