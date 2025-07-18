CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(100) PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS contacts (
    contact_id UUID PRIMARY KEY,
    name VARCHAR(140) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    added_on TIMESTAMP NOT NULL,
    birthday DATE,
    CONSTRAINT fk_users
        FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS emails (
    mark VARCHAR(25) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contact_id UUID NOT NULL,
    CONSTRAINT fk_contacts
        FOREIGN KEY (contact_id)
        REFERENCES contacts (contact_id)
        ON DELETE CASCADE,
    PRIMARY KEY (mark, contact_id)
);

CREATE TABLE IF NOT EXISTS phone_numbers (
    mark VARCHAR(25) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    contact_id UUID NOT NULL,
    CONSTRAINT fk_contacts
        FOREIGN KEY (contact_id)
        REFERENCES contacts (contact_id)
        ON DELETE CASCADE,
    PRIMARY KEY (mark, contact_id)
);

CREATE TABLE IF NOT EXISTS addresses (
    mark VARCHAR(25) NOT NULL,
    country VARCHAR(20) NOT NULL,
    street VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(20) NOT NULL,
    zipcode VARCHAR(15) NOT NULL,
    contact_id UUID NOT NULL,
    CONSTRAINT fk_contacts
        FOREIGN KEY (contact_id)
        REFERENCES contacts (contact_id)
        ON DELETE CASCADE,
    PRIMARY KEY (mark, contact_id)
);
