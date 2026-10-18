# Brainics Backend - Phase 1 Testing Guide

## Overview
This guide covers manual and automated testing for Phase 1 of the Brainics backend.

---

## Automated Testing

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=EnquiryServiceIntegrationTest
mvn test -Dtest=AcademyControllerIntegrationTest
```

### Run Tests by Pattern
```bash
# Run all service tests
mvn test -Dtest=*ServiceIntegrationTest

# Run all controller tests
mvn test -Dtest=*ControllerIntegrationTest
```

### Test Coverage
```bash
mvn clean test jacoco:report
# Report located at: target/site/jacoco/index.html
```

---

## Manual Testing with Resend

### 1. Setup Resend Credentials

1. Get your Resend API key from https://resend.com
2. Add to `.env` file in project root:
   ```
   RESEND_API_KEY=your_resend_api_key_here
   BRAINICS_EMAIL_FROM=noreply@brainics.com
   BRAINICS_EMAIL_REPLY_TO=support@brainics.com
   ADMIN_ENQUIRY_EMAIL=admin@brainics.com
   ```

### 2. Start the Application

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### 3. Test Enquiry Submission (API Call)

**cURL Request:**
```bash
curl -X POST http://localhost:8080/api/public/enquiry/submit \
  -H "Content-Type: application/json" \
  -d '{
    "parentName": "Ahmed Ali",
    "parentEmail": "test@example.com",
    "parentPhone": "+923001234567",
    "studentName": "Ali Ahmed",
    "classId": 1,
    "preferredSubjectIds": [1, 2],
    "message": "Interested in Math and Science"
  }'
```

**Expected Response (201 Created):**
```json
{
  "status": "success",
  "message": "Enquiry submitted successfully",
  "data": {
    "id": 1,
    "parentName": "Ahmed Ali",
    "parentEmail": "test@example.com",
    "parentPhone": "+923001234567",
    "studentName": "Ali Ahmed",
    "className": "Class 1",
    "status": "NEW",
    "createdAt": "2026-10-18T10:30:00Z"
  }
}
```

### 4. Verify Email Notifications

#### Admin Email
- Check the admin email (configured in `ADMIN_ENQUIRY_EMAIL`)
- Should contain:
  - Student name and class level
  - Parent contact information
  - Preferred subjects
  - Link to admin dashboard

#### Parent Email
- Check the parent email (from the form)
- Should contain:
  - Confirmation message
  - Enquiry reference ID
  - Status tracking link
  - Preferred subjects

### 5. Test Duplicate Prevention

Submit the same enquiry twice within 24 hours:
```bash
# First submission - should succeed (201)
curl -X POST http://localhost:8080/api/public/enquiry/submit \
  -H "Content-Type: application/json" \
  -d '{
    "parentName": "Test User",
    "parentEmail": "duplicate@example.com",
    "parentPhone": "+923001234567",
    "studentName": "Test Student",
    "classId": 1
  }'

# Second submission - should fail (400)
curl -X POST http://localhost:8080/api/public/enquiry/submit \
  -H "Content-Type: application/json" \
  -d '{
    "parentName": "Test User",
    "parentEmail": "duplicate@example.com",
    "parentPhone": "+923001234567",
    "studentName": "Test Student",
    "classId": 1
  }'
```

### 6. Test Rate Limiting

Submit 6 enquiries from the same IP within 1 hour:
```bash
for i in {1..6}; do
  curl -X POST http://localhost:8080/api/public/enquiry/submit \
    -H "Content-Type: application/json" \
    -d "{
      \"parentName\": \"Rate Test $i\",
      \"parentEmail\": \"rate-test-$i@example.com\",
      \"parentPhone\": \"+923001234567\",
      \"studentName\": \"Student $i\",
      \"classId\": 1
    }"
done
```

- First 5 should succeed (201)
- 6th should fail with 429 Too Many Requests

### 7. Test Validation

#### Invalid Email Format
```bash
curl -X POST http://localhost:8080/api/public/enquiry/submit \
  -H "Content-Type: application/json" \
  -d '{
    "parentName": "Test",
    "parentEmail": "invalid-email",
    "parentPhone": "+923001234567",
    "studentName": "Test",
    "classId": 1
  }'
# Expected: 400 Bad Request
```

#### Invalid Phone Format
```bash
curl -X POST http://localhost:8080/api/public/enquiry/submit \
  -H "Content-Type: application/json" \
  -d '{
    "parentName": "Test",
    "parentEmail": "test@example.com",
    "parentPhone": "12345",
    "studentName": "Test",
    "classId": 1
  }'
# Expected: 400 Bad Request
```

#### Missing Required Fields
```bash
curl -X POST http://localhost:8080/api/public/enquiry/submit \
  -H "Content-Type: application/json" \
  -d '{
    "parentName": "Test"
  }'
# Expected: 400 Bad Request
```

---

## API Endpoint Testing

### Academy Endpoints (Public)

**Get All Classes:**
```bash
curl http://localhost:8080/api/public/academy/classes
```

**Get Class by ID:**
```bash
curl http://localhost:8080/api/public/academy/classes/1
```

**Get All Subjects:**
```bash
curl http://localhost:8080/api/public/academy/subjects
```

**Get Subject by ID:**
```bash
curl http://localhost:8080/api/public/academy/subjects/1
```

### Organization Endpoints (Public)

**Get Organization Info:**
```bash
curl http://localhost:8080/api/public/organization/info
```

### Localization Endpoints (Public)

**Get All Translations (English):**
```bash
curl "http://localhost:8080/api/public/i18n/keys?lang=en"
```

**Get All Translations (Urdu):**
```bash
curl "http://localhost:8080/api/public/i18n/keys?lang=ur"
```

**Get Translations by Category:**
```bash
curl "http://localhost:8080/api/public/i18n/keys?lang=en&category=enquiry"
```

### Admin Endpoints (Requires Authentication)

**Get All Enquiries:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/admin/enquiries
```

**Get Enquiries by Status:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/admin/enquiries/status/NEW
```

---

## Email Delivery Verification Checklist

- [ ] Admin email received for new enquiry
- [ ] Parent email received for new enquiry
- [ ] Email subject is correct
- [ ] Template variables replaced correctly
- [ ] All links in emails are functional
- [ ] Email styling is correct
- [ ] No HTML encoding issues
- [ ] Sender address is correct (noreply@brainics.com)
- [ ] Reply-to address is correct

---

## Performance Testing

### Load Test: 5+ Submissions/Second

```bash
# Using Apache Bench
ab -n 100 -c 10 -p enquiry.json -T application/json http://localhost:8080/api/public/enquiry/submit

# enquiry.json content:
{
  "parentName": "Load Test User",
  "parentEmail": "load-test@example.com",
  "parentPhone": "+923001234567",
  "studentName": "Load Test Student",
  "classId": 1
}
```

Expected: All requests complete within reasonable time (<5 seconds average)

---

## Database Verification

### Check if Migrations Applied

```bash
# Connect to database
psql -h localhost -U postgres -d brainics

# Verify tables exist
\dt academy.* enquiry.* i18n.* organization.* notification.*

# Check notification templates
SELECT id, name, type, language_code, is_active FROM notification_templates;

# Check localization entries
SELECT COUNT(*) FROM localization WHERE is_active = true;
```

---

## Troubleshooting

### Email Not Sending

1. **Check Resend API Key**
   - Verify API key is correct in `.env`
   - Check Resend dashboard for recent errors

2. **Check Email Logs**
   - Look for warnings in application logs
   - Filter for "NotificationService"

3. **Verify Template Exists**
   - Query notification_templates table
   - Ensure template name matches service code

### Rate Limiting Issues

- Rate limits reset every hour
- Clear browser cookies if testing locally
- Use different IPs or wait 1 hour to retry

### Duplicate Enquiry Rejection

- Check error message: "enquiry already submitted in the last 24 hours"
- Wait 24 hours or modify duplicate-check-hours in config
- Use different parent email for testing

---

## Test Results Summary Template

```
Test Date: ___________
Tester: ___________

Automation Tests:
- Unit Tests: PASS / FAIL
- Integration Tests: PASS / FAIL
- Test Coverage: ____%

Manual Tests:
- Enquiry Submission: PASS / FAIL
- Email Delivery (Admin): PASS / FAIL
- Email Delivery (Parent): PASS / FAIL
- Duplicate Prevention: PASS / FAIL
- Rate Limiting: PASS / FAIL
- Validation (Email): PASS / FAIL
- Validation (Phone): PASS / FAIL

Performance:
- 5+ TPS Achieved: YES / NO
- Average Response Time: _____ ms

Issues Found:
1. ___________
2. ___________

Sign-off: ___________
```

---

## Next Steps

After successful testing:
1. Deploy to staging environment
2. Run smoke tests in staging
3. Get stakeholder approval
4. Deploy to production
5. Monitor email delivery in production
