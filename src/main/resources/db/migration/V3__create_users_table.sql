CREATE TABLE users(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    username VARCHAR(50) not null unique ,
    password_hash VARCHAR(255) not null ,
    role VARCHAR(50) not null default 'ROLE_USER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_username ON users(username);