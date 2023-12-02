CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS contacts (
    contact_id UUID PRIMARY KEY,
    name VARCHAR(140) NOT NULL,
    type VARCHAR(15) NOT NULL,
    user_id UUID NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS emails (
    email_id UUID PRIMARY KEY,
    type VARCHAR(15) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contact_id UUID NOT NULL UNIQUE,
    FOREIGN KEY (contact_id) REFERENCES contacts (contact_id)
);

CREATE TABLE IF NOT EXISTS phone_numbers (
    phone_number_id UUID PRIMARY KEY,
    type VARCHAR(15) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    contact_id UUID NOT NULL UNIQUE,
    FOREIGN KEY (contact_id) REFERENCES contacts (contact_id)
);

CREATE TABLE IF NOT EXISTS address (
    address_id UUID PRIMARY KEY,
    country VARCHAR(20) NOT NULL,
    street VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    contact_id UUID NOT NULL UNIQUE,
    FOREIGN KEY (contact_id) REFERENCES contacts (contact_id)
);
