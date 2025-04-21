CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    balance       NUMERIC(10, 2)                                DEFAULT 0.00,
    role          VARCHAR(20) CHECK (role IN ('user', 'admin')) DEFAULT 'user',
    created_at    TIMESTAMP                                     DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP                                     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE lots
(
    id             SERIAL PRIMARY KEY,
    title          VARCHAR(100)   NOT NULL,
    description    TEXT,
    starting_price NUMERIC(10, 2) NOT NULL,
    current_price  NUMERIC(10, 2) NOT NULL,
    seller_id      INT            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    category_id    INT            REFERENCES categories (id) ON DELETE SET NULL,
    status         VARCHAR(20) CHECK (status IN ('active', 'closed', 'sold')) DEFAULT 'active',
    created_at     TIMESTAMP                                                  DEFAULT CURRENT_TIMESTAMP,
    start_time     TIMESTAMP      NOT NULL                                    DEFAULT CURRENT_TIMESTAMP,
    end_time       TIMESTAMP      NOT NULL                                    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bids
(
    id         SERIAL PRIMARY KEY,
    lot_id    INT            NOT NULL REFERENCES lots (id) ON DELETE CASCADE,
    user_id    INT            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    amount     NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions
(
    id         SERIAL PRIMARY KEY,
    user_id    INT                                               NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    lot_id    INT                                               NOT NULL REFERENCES lots (id) ON DELETE CASCADE,
    amount     NUMERIC(10, 2)                                    NOT NULL,
    type       VARCHAR(20) CHECK (type IN ('payment', 'refund')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE images
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    size        INT NOT NULL,
    lot_id     INT          NOT NULL REFERENCES lots (id) ON DELETE CASCADE,
    key         VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);