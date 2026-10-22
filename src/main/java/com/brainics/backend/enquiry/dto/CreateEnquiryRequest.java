package com.brainics.backend.enquiry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEnquiryRequest {

    @NotBlank(message = "Parent name is required")
    private String parentName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Parent email is required")
    private String parentEmail;

    @NotBlank(message = "Parent phone is required")
    @Pattern(regexp = "^(\\+91|0)[6-9][0-9]{9}$", message = "Phone must be in format +91XXXXXXXXXX or 0XXXXXXXXXX")
    private String parentPhone;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate studentDateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE or OTHER")
    private String gender;

    @Size(max = 200, message = "Current school must be at most 200 characters")
    private String currentSchool;

    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    @NotNull(message = "Class is required")
    private Long classId;

    private List<Long> preferredSubjectIds;

    private String message;
}
