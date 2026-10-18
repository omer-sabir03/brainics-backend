-- Create notification_templates table for email/SMS/WhatsApp templates
CREATE TABLE notification_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EMAIL', 'SMS', 'WHATSAPP')),
    language_code VARCHAR(5) NOT NULL DEFAULT 'en' CHECK (language_code IN ('en', 'ur')),
    subject VARCHAR(255),
    body TEXT NOT NULL,
    variables JSONB DEFAULT '[]',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_templates_name ON notification_templates(name);
CREATE INDEX idx_notification_templates_type ON notification_templates(type);
CREATE INDEX idx_notification_templates_language ON notification_templates(language_code);

-- Insert notification templates for Phase 1
-- Template: enquiry_received_admin (English)
INSERT INTO notification_templates (name, type, language_code, subject, body, variables, is_active) VALUES (
    'enquiry_received_admin',
    'EMAIL',
    'en',
    'New Enquiry: {{studentName}} for Class {{classLevel}}',
    '<h2>New Admission Enquiry</h2>
<p>You have received a new admission enquiry:</p>
<ul>
  <li><strong>Student Name:</strong> {{studentName}}</li>
  <li><strong>Class Interested:</strong> Class {{classLevel}}</li>
  <li><strong>Parent Name:</strong> {{parentName}}</li>
  <li><strong>Email:</strong> {{parentEmail}}</li>
  <li><strong>Phone:</strong> {{parentPhone}}</li>
  <li><strong>Preferred Subjects:</strong> {{subjects}}</li>
  <li><strong>Submitted At:</strong> {{submittedAt}}</li>
</ul>
<p><a href="{{dashboardUrl}}">View in Dashboard</a></p>',
    '["studentName", "classLevel", "parentName", "parentEmail", "parentPhone", "subjects", "submittedAt", "dashboardUrl"]',
    TRUE
);

-- Template: enquiry_received_parent (English)
INSERT INTO notification_templates (name, type, language_code, subject, body, variables, is_active) VALUES (
    'enquiry_received_parent',
    'EMAIL',
    'en',
    'Thank you for your enquiry - Reference: {{enquiryId}}',
    '<h2>Thank You for Your Enquiry</h2>
<p>Dear {{parentName}},</p>
<p>Thank you for submitting your admission enquiry to Brainics. We have received your application for {{studentName}} for Class {{classLevel}}.</p>
<p><strong>Your Enquiry Reference:</strong> {{enquiryId}}</p>
<p>We will review your enquiry and contact you within 24-48 hours at {{parentPhone}} or {{parentEmail}}.</p>
<p>You can track your enquiry status at: <a href="{{statusUrl}}">Check Status</a></p>
<p><strong>Preferred Subjects:</strong> {{subjects}}</p>
<p>Thank you for choosing Brainics!</p>
<p>Best regards,<br/>Brainics Team</p>',
    '["parentName", "studentName", "classLevel", "enquiryId", "parentPhone", "parentEmail", "statusUrl", "subjects"]',
    TRUE
);

-- Template: enquiry_received_admin (Urdu)
INSERT INTO notification_templates (name, type, language_code, subject, body, variables, is_active) VALUES (
    'enquiry_received_admin_ur',
    'EMAIL',
    'ur',
    'نئی درخواست: {{studentName}} کلاس {{classLevel}} کے لیے',
    '<h2>نئی داخلہ درخواست</h2>
<p>آپ کو ایک نئی داخلہ درخواست موصول ہوئی ہے:</p>
<ul>
  <li><strong>طالب علم کا نام:</strong> {{studentName}}</li>
  <li><strong>دلچسپی کی کلاس:</strong> کلاس {{classLevel}}</li>
  <li><strong>والدین کا نام:</strong> {{parentName}}</li>
  <li><strong>ای میل:</strong> {{parentEmail}}</li>
  <li><strong>فون:</strong> {{parentPhone}}</li>
  <li><strong>پسندیدہ مضامین:</strong> {{subjects}}</li>
</ul>
<p><a href="{{dashboardUrl}}">ڈیش بورڈ میں دیکھیں</a></p>',
    '["studentName", "classLevel", "parentName", "parentEmail", "parentPhone", "subjects", "dashboardUrl"]',
    TRUE
);

-- Template: enquiry_received_parent (Urdu)
INSERT INTO notification_templates (name, type, language_code, subject, body, variables, is_active) VALUES (
    'enquiry_received_parent_ur',
    'EMAIL',
    'ur',
    'آپ کی درخواست کے لیے شکریہ - حوالہ: {{enquiryId}}',
    '<h2>آپ کی درخواست کے لیے شکریہ</h2>
<p>محترم {{parentName}},</p>
<p>برینکس میں داخلہ کی درخواست جمع کرنے کے لیے آپ کا شکریہ۔ ہم نے {{studentName}} کی کلاس {{classLevel}} کے لیے آپ کی درخواست موصول کی ہے۔</p>
<p><strong>آپ کی درخواست کا حوالہ:</strong> {{enquiryId}}</p>
<p>ہم آپ کی درخواست کا جائزہ لیں گے اور 24-48 گھنٹوں میں {{parentPhone}} یا {{parentEmail}} پر آپ سے رابطہ کریں گے۔</p>
<p>آپ یہاں اپنی درخواست کی حالت دیکھ سکتے ہیں: <a href="{{statusUrl}}">حالت چیک کریں</a></p>
<p><strong>پسندیدہ مضامین:</strong> {{subjects}}</p>
<p>برینکس منتخب کرنے کے لیے شکریہ!</p>
<p>بہترین تمام،<br/>برینکس ٹیم</p>',
    '["parentName", "studentName", "classLevel", "enquiryId", "parentPhone", "parentEmail", "statusUrl", "subjects"]',
    TRUE
);

-- Placeholder templates for WhatsApp and SMS (Phase 2)
INSERT INTO notification_templates (name, type, language_code, subject, body, variables, is_active) VALUES (
    'enquiry_status_update_whatsapp',
    'WHATSAPP',
    'en',
    '',
    'Hi {{parentName}}, your enquiry for {{studentName}} (Class {{classLevel}}) status is now: {{status}}. We will contact you soon.',
    '["parentName", "studentName", "classLevel", "status"]',
    FALSE
);

INSERT INTO notification_templates (name, type, language_code, subject, body, variables, is_active) VALUES (
    'enquiry_confirmation_sms',
    'SMS',
    'en',
    '',
    'Thank you for your enquiry to Brainics. Your reference: {{enquiryId}}. We will contact you within 24-48 hours.',
    '["enquiryId"]',
    FALSE
);
