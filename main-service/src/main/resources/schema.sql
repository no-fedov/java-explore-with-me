DROP TABLE IF EXISTS compilation_event CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS users_app CASCADE;

DROP SEQUENCE IF EXISTS user_seq CASCADE;
DROP SEQUENCE IF EXISTS category_seq CASCADE;
DROP SEQUENCE IF EXISTS event_seq CASCADE;
DROP SEQUENCE IF EXISTS request_seq CASCADE;
DROP SEQUENCE IF EXISTS compilation_seq CASCADE;

CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 0 INCREMENT BY 1 MINVALUE 0;

CREATE TABLE IF NOT EXISTS users_app (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_seq'),
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255)
);

CREATE SEQUENCE IF NOT EXISTS category_seq START WITH 0 INCREMENT BY 1 MINVALUE 0;

CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY DEFAULT nextval('category_seq'),
    name VARCHAR(255) UNIQUE NOT NULL,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS location (
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (lat, lon)
);

CREATE SEQUENCE IF NOT EXISTS event_seq START WITH 0 INCREMENT BY 1 MINVALUE 0;

CREATE TABLE IF NOT EXISTS events (
    id BIGINT PRIMARY KEY DEFAULT nextval('event_seq'),
    initiator_id BIGINT REFERENCES users_app(id),
    category_id BIGINT REFERENCES category(id),
    title VARCHAR(120),
    annotation VARCHAR(2000),
    description VARCHAR(7000),
    paid BOOLEAN,
    request_moderation BOOLEAN,
    participant_limit BIGINT,
    time TIMESTAMP,
    state VARCHAR(255),
    location_lat DOUBLE PRECISION,
    location_lon DOUBLE PRECISION,
    created_on TIMESTAMP,
    published_on TIMESTAMP,
    CONSTRAINT fk_location FOREIGN KEY (location_lat, location_lon) REFERENCES location(lat, lon)
);

CREATE SEQUENCE IF NOT EXISTS request_seq START WITH 0 INCREMENT BY 1 MINVALUE 0;

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT PRIMARY KEY DEFAULT nextval('request_seq'),
    event_id BIGINT REFERENCES events(id),
    requester_id BIGINT REFERENCES users_app(id),
    status VARCHAR(255),
    created TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS compilation_seq START WITH 0 INCREMENT BY 1 MINVALUE 0;

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT PRIMARY KEY DEFAULT nextval('compilation_seq'),
    pinned BOOLEAN,
    title VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT REFERENCES compilations(id),
    event_id BIGINT REFERENCES events(id),
    PRIMARY KEY (compilation_id, event_id)
);

