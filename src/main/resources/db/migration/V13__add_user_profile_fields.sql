-- Add user profile fields for admin user management
ALTER TABLE users
ADD COLUMN first_name VARCHAR(100),
ADD COLUMN last_name VARCHAR(100),
ADD COLUMN phone VARCHAR(20);

-- Add index for phone lookup
CREATE INDEX idx_users_phone ON users(phone);
