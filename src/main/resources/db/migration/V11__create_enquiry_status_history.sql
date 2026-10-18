-- Create enquiry_status_history table
CREATE TABLE enquiry_status_history (
    id BIGSERIAL PRIMARY KEY,
    enquiry_id BIGINT NOT NULL REFERENCES admission_enquiries(id) ON DELETE CASCADE,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_by_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reason TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_enquiry_status_history_enquiry_id ON enquiry_status_history(enquiry_id);
CREATE INDEX idx_enquiry_status_history_changed_by_id ON enquiry_status_history(changed_by_id);
CREATE INDEX idx_enquiry_status_history_new_status ON enquiry_status_history(new_status);
CREATE INDEX idx_enquiry_status_history_created_at ON enquiry_status_history(created_at DESC);

-- Add assigned_to_id to admission_enquiries if not already there (for tracking who is handling the enquiry)
ALTER TABLE admission_enquiries
ADD COLUMN IF NOT EXISTS assigned_to_id BIGINT REFERENCES users(id) ON DELETE SET NULL;

-- Add index for assigned_to queries
CREATE INDEX IF NOT EXISTS idx_admission_enquiries_assigned_to_id ON admission_enquiries(assigned_to_id);
