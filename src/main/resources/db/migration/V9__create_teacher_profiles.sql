-- Create teacher_profiles table
CREATE TABLE teacher_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    bio TEXT,
    qualifications TEXT,
    experience_years SMALLINT,
    phone VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_teacher_profiles_user_id ON teacher_profiles(user_id);
CREATE INDEX idx_teacher_profiles_created_at ON teacher_profiles(created_at DESC);

-- Create teacher_subject_expertise junction table
CREATE TABLE teacher_subject_expertise (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL REFERENCES teacher_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    proficiency_level VARCHAR(20) DEFAULT 'INTERMEDIATE' CHECK (proficiency_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_teacher_subject_expertise ON teacher_subject_expertise(teacher_id, subject_id);
CREATE INDEX idx_teacher_subject_expertise_teacher_id ON teacher_subject_expertise(teacher_id);
CREATE INDEX idx_teacher_subject_expertise_subject_id ON teacher_subject_expertise(subject_id);
