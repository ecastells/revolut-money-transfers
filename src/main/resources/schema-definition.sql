DROP TABLE currency;
CREATE TABLE currency (
  id INT NOT NULL,
  type VARCHAR(40) NOT NULL,
  PRIMARY KEY ( id )
);

DROP TABLE account;
CREATE TABLE account (
  id IDENTITY,
  owner VARCHAR(200) NOT NULL,
  balance DECIMAL(19,2) NOT NULL,
  pendingTransfer DECIMAL(19,2) NOT NULL,
  currency_id INT NOT NULL,
  FOREIGN KEY(currency_id) REFERENCES currency(id)
);