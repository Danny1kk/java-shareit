DROP TABLE IF EXISTS users, items, bookings, requests, comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS requests
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(2000),
    requester_id BIGINT NOT NULL,
    CONSTRAINT fk_requests_author
    FOREIGN KEY (requester_id)
    REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS items
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT fk_items_owner
    FOREIGN KEY (owner_id)
    REFERENCES users (id),
    CONSTRAINT fk_items_request
    FOREIGN KEY (request_id)
    REFERENCES requests (id)
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_bookings_item
    FOREIGN KEY (item_id)
    REFERENCES items (id),
    CONSTRAINT fk_bookings_booker
    FOREIGN KEY (booker_id)
    REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS comments
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(2000),
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_item
    FOREIGN KEY (item_id)
    REFERENCES items (id),
    CONSTRAINT fk_comments_author
    FOREIGN KEY (author_id)
    REFERENCES users (id)
    );