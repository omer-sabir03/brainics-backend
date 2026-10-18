-- Create admission_enquiries table
CREATE TABLE admission_enquiries (
    id BIGSERIAL PRIMARY KEY,
    parent_name VARCHAR(100) NOT NULL,
    parent_email VARCHAR(254) NOT NULL,
    parent_phone VARCHAR(20) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    class_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE RESTRICT,
    message TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'REVIEWED', 'CONTACTED', 'ENROLLED', 'CLOSED')),
    source VARCHAR(30) DEFAULT 'WEB' CHECK (source IN ('WEB', 'WHATSAPP', 'PHONE', 'REFERRAL')),
    assigned_to_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_ip_address VARCHAR(45)
);

CREATE INDEX idx_admission_enquiries_parent_email ON admission_enquiries(LOWER(parent_email));
CREATE INDEX idx_admission_enquiries_parent_phone ON admission_enquiries(parent_phone);
CREATE INDEX idx_admission_enquiries_status ON admission_enquiries(status);
CREATE INDEX idx_admission_enquiries_class_id ON admission_enquiries(class_id);
CREATE INDEX idx_admission_enquiries_created_at ON admission_enquiries(created_at DESC);
CREATE INDEX idx_admission_enquiries_assigned_to ON admission_enquiries(assigned_to_id);

-- Create enquiry_subjects junction table (many-to-many)
CREATE TABLE enquiry_subjects (
    id BIGSERIAL PRIMARY KEY,
    enquiry_id BIGINT NOT NULL REFERENCES admission_enquiries(id) ON DELETE CASCADE,
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_enquiry_subjects_unique ON enquiry_subjects(enquiry_id, subject_id);
CREATE INDEX idx_enquiry_subjects_enquiry_id ON enquiry_subjects(enquiry_id);
CREATE INDEX idx_enquiry_subjects_subject_id ON enquiry_subjects(subject_id);

-- Create enquiry_notes table (for Phase 2 - admin notes)
CREATE TABLE enquiry_notes (
    id BIGSERIAL PRIMARY KEY,
    enquiry_id BIGINT NOT NULL REFERENCES admission_enquiries(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_enquiry_notes_enquiry_id ON enquiry_notes(enquiry_id);
CREATE INDEX idx_enquiry_notes_created_at ON enquiry_notes(created_at DESC);
