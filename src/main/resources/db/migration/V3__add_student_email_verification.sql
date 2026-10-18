ALTER TABLE users
    ADD COLUMN email VARCHAR(254),
    ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT TRUE;

CREATE UNIQUE INDEX uq_users_email_lower
    ON users (LOWER(email))
    WHERE email IS NOT NULL;

CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_email_verification_tokens_expires_at
    ON email_verification_tokens (expires_at);
