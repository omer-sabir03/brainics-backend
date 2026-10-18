-- Add first response time tracking for enquiries
ALTER TABLE admission_enquiries
ADD COLUMN first_response_time TIMESTAMPTZ;

-- Add index for response time analysis
CREATE INDEX idx_admission_enquiries_first_response_time ON admission_enquiries(first_response_time);
