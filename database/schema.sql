DROP TABLE IF EXISTS Account;
CREATE TABLE Account (
  id INTEGER PRIMARY KEY,
  email TEXT,
  salt TEXT,
  password TEXT,
  first_name TEXT,
  last_name TEXT
);

DROP TABLE IF EXISTS Wallet;
CREATE TABLE Wallet (
  id INTEGER PRIMARY KEY,
  name TEXT,
  owner INTEGER,
  FOREIGN KEY(owner) REFERENCES Account(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS Category;
CREATE TABLE Category (
  id INTEGER PRIMARY KEY,
  name TEXT,
  amount REAL,
  wallet INTEGER,
  FOREIGN KEY(wallet) REFERENCES Wallet(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS Movement;
CREATE TABLE Movement (
  id INTEGER PRIMARY KEY,
  name TEXT,
  description TEXT,
  amount REAL,
  category INTEGER,
  FOREIGN KEY(category) REFERENCES Category(id) ON DELETE CASCADE
);
