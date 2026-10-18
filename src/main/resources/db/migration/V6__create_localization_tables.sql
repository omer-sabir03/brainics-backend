-- Create localization table for multi-language support (EN, UR)
CREATE TABLE localization (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(255) NOT NULL,
    language_code VARCHAR(5) NOT NULL CHECK (language_code IN ('en', 'ur')),
    value TEXT NOT NULL,
    category VARCHAR(50) DEFAULT 'general' CHECK (category IN ('general', 'academy', 'enquiry', 'nav', 'footer', 'email', 'admin')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_localization_key_lang ON localization(key, language_code);
CREATE INDEX idx_localization_language_code ON localization(language_code);
CREATE INDEX idx_localization_category ON localization(category);
CREATE INDEX idx_localization_is_active ON localization(is_active);

-- Insert sample translations for Phase 1
INSERT INTO localization (key, language_code, value, category, is_active) VALUES
-- Academy - English
('academy.title', 'en', 'Classes & Subjects', 'academy', TRUE),
('academy.classes', 'en', 'Available Classes', 'academy', TRUE),
('academy.subjects', 'en', 'Available Subjects', 'academy', TRUE),
('academy.class_level', 'en', 'Class', 'academy', TRUE),

-- Academy - Urdu
('academy.title', 'ur', 'جماعتیں اور مضامین', 'academy', TRUE),
('academy.classes', 'ur', 'دستیاب جماعتیں', 'academy', TRUE),
('academy.subjects', 'ur', 'دستیاب مضامین', 'academy', TRUE),
('academy.class_level', 'ur', 'جماعت', 'academy', TRUE),

-- Enquiry - English
('enquiry.form_title', 'en', 'Admission Enquiry', 'enquiry', TRUE),
('enquiry.parent_name', 'en', 'Parent Name', 'enquiry', TRUE),
('enquiry.parent_email', 'en', 'Email Address', 'enquiry', TRUE),
('enquiry.parent_phone', 'en', 'Phone Number', 'enquiry', TRUE),
('enquiry.student_name', 'en', 'Student Name', 'enquiry', TRUE),
('enquiry.class_interested', 'en', 'Class Interested', 'enquiry', TRUE),
('enquiry.subjects', 'en', 'Preferred Subjects', 'enquiry', TRUE),
('enquiry.message', 'en', 'Additional Message', 'enquiry', TRUE),
('enquiry.submit', 'en', 'Submit Enquiry', 'enquiry', TRUE),
('enquiry.success', 'en', 'Thank you! Your enquiry has been submitted successfully.', 'enquiry', TRUE),
('enquiry.error', 'en', 'An error occurred while submitting your enquiry. Please try again.', 'enquiry', TRUE),

-- Enquiry - Urdu
('enquiry.form_title', 'ur', 'داخلہ کے بارے میں معلومات حاصل کریں', 'enquiry', TRUE),
('enquiry.parent_name', 'ur', 'والدین کا نام', 'enquiry', TRUE),
('enquiry.parent_email', 'ur', 'ای میل پتہ', 'enquiry', TRUE),
('enquiry.parent_phone', 'ur', 'فون نمبر', 'enquiry', TRUE),
('enquiry.student_name', 'ur', 'طالب علم کا نام', 'enquiry', TRUE),
('enquiry.class_interested', 'ur', 'دلچسپی کی جماعت', 'enquiry', TRUE),
('enquiry.subjects', 'ur', 'پسندیدہ مضامین', 'enquiry', TRUE),
('enquiry.message', 'ur', 'اضافی پیغام', 'enquiry', TRUE),
('enquiry.submit', 'ur', 'درخواست جمع کریں', 'enquiry', TRUE),
('enquiry.success', 'ur', 'شکریہ! آپ کی درخواست کامیابی سے جمع ہو گئی۔', 'enquiry', TRUE),
('enquiry.error', 'ur', 'درخواست جمع کرتے وقت خرابی ہوئی۔ براہ کرم دوبارہ کوشش کریں۔', 'enquiry', TRUE);
