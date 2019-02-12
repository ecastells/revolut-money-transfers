INSERT INTO currency
VALUES (1, 'ARG'), (2, 'EUR'), (3, 'USD'), (4, 'GBP');

INSERT INTO currency_conversion
VALUES (1, 1, 2, 0.023), (2, 1, 3, 0.026), (3, 1, 4, 0.02),
       (4, 2, 1, 42.85), (5, 2, 3, 1.13), (6, 2, 4, 0.88),
       (7, 3, 1, 37.80), (8, 3, 2, 0.88), (9, 3, 4, 0.77),
       (10, 4, 1, 48.96), (11, 4, 2, 1.14), (12, 4, 2, 1.30);

INSERT INTO account (owner, balance, currency_id)
VALUES
('Emiliano Castells', 2000.75, 2),
('Grecia Zoppi', 5000, 1);