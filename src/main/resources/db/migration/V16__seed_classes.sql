-- Seed school classes 1-10 so the admission form has classes to choose from.
-- Safe to re-run: existing class numbers are left untouched.
INSERT INTO classes (class_number, description, is_active)
SELECT n, 'Class ' || n, TRUE
FROM generate_series(1, 10) AS n
ON CONFLICT (class_number) DO NOTHING;
