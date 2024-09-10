DROP TABLE IF EXISTS hits;
DROP TABLE IF EXISTS apps;
DROP TABLE IF EXISTS ip_addresses;

CREATE TABLE IF NOT EXISTS apps (
    id INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS ip_addresses (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    address VARCHAR
);

CREATE TABLE IF NOT EXISTS hits (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    fk_app BIGINT REFERENCES apps (id),
    fk_ip BIGINT REFERENCES ip_addresses (id),
    uri VARCHAR(512),
    time TIMESTAMP
);