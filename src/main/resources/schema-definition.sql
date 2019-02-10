DROP TABLE IF EXISTS currency;
CREATE TABLE currency (
  id INT NOT NULL,
  type VARCHAR(40) NOT NULL,
  PRIMARY KEY ( id )
);

DROP TABLE IF EXISTS currency_conversion;
CREATE TABLE currency_conversion (
  id INT NOT NULL,
  from_currency_id INT NOT NULL,
  to_currency_id INT NOT NULL,
  rate_change DECIMAL(19,4) NOT NULL,
  PRIMARY KEY ( id ),
  FOREIGN KEY(from_currency_id) REFERENCES currency(id),
  FOREIGN KEY(to_currency_id) REFERENCES currency(id)
);

DROP TABLE IF EXISTS account;
CREATE TABLE account (
  id IDENTITY,
  owner VARCHAR(200) NOT NULL,
  balance DECIMAL(19,2) NOT NULL,
  pending_transfer DECIMAL(19,2) NOT NULL,
  currency_id INT NOT NULL,
  FOREIGN KEY(currency_id) REFERENCES currency(id)
);

DROP TABLE IF EXISTS transaction;
CREATE TABLE transaction (
  id IDENTITY,
  from_account_id BIGINT NOT NULL,
  amount DECIMAL(19,2) NOT NULL,
  currency_id INT NOT NULL,
  to_account_id BIGINT NOT NULL,
  status VARCHAR(40) NOT NULL,
  creation_date TIMESTAMP NOT NULL,
  last_update_date TIMESTAMP,
  FOREIGN KEY(from_account_id) REFERENCES account(id),
  FOREIGN KEY(to_account_id) REFERENCES account(id),
  FOREIGN KEY(currency_id) REFERENCES currency(id)
);