-- Create classes table (school classes 1-10)
CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    class_number SMALLINT NOT NULL UNIQUE CHECK (class_number >= 1 AND class_number <= 10),
    description VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_classes_class_number ON classes(class_number);

-- Create subjects table (subjects offered)
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name_en VARCHAR(100) NOT NULL,
    name_urdu VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_subjects_name_en ON subjects(LOWER(name_en));

-- Create class_subjects junction table (many-to-many relationship)
CREATE TABLE class_subjects (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_class_subjects_unique ON class_subjects(class_id, subject_id);
CREATE INDEX idx_class_subjects_class_id ON class_subjects(class_id);
CREATE INDEX idx_class_subjects_subject_id ON class_subjects(subject_id);
