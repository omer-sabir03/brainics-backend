-- Create organization_info table for contact details and location info
CREATE TABLE organization_info (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(254) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Pakistan',
    website_url VARCHAR(255),
    social_media_links JSONB DEFAULT '{}',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    office_hours JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Note: This is a single-record table. Only one row should exist at a time.
-- The application should enforce this constraint.
CREATE INDEX idx_organization_info_email ON organization_info(email);

-- Insert default organization info (can be updated by admin in Phase 2)
INSERT INTO organization_info (
    name,
    phone_primary,
    email,
    address,
    city,
    country,
    social_media_links,
    office_hours
) VALUES (
    'Brainics Tuition',
    '+92-XXX-XXXXXXX',
    'info@brainics.com',
    'Brainics Institute Address',
    'Islamabad',
    'Pakistan',
    '{"facebook": "", "twitter": "", "instagram": "", "whatsapp": ""}',
    '{"monday": {"open": "09:00", "close": "17:00"}, "tuesday": {"open": "09:00", "close": "17:00"}, "wednesday": {"open": "09:00", "close": "17:00"}, "thursday": {"open": "09:00", "close": "17:00"}, "friday": {"open": "09:00", "close": "17:00"}, "saturday": {"open": "10:00", "close": "14:00"}, "sunday": {"open": "close"}}'
);
