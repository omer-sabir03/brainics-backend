-- Student details collected by the public admission form (all optional, so existing enquiries are unaffected)
ALTER TABLE admission_enquiries
ADD COLUMN student_date_of_birth DATE,
ADD COLUMN gender VARCHAR(10),
ADD COLUMN current_school VARCHAR(200),
ADD COLUMN address VARCHAR(500);

ALTER TABLE admission_enquiries
ADD CONSTRAINT chk_admission_enquiries_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'));
