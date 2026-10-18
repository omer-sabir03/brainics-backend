package com.brainics.backend.enquiry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Pattern(regexp = "^(\\+92|0)[0-9]{10}$", message = "Phone must be in format +92XXXXXXXXXX or 0XXXXXXXXXX")
    private String parentPhone;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotNull(message = "Class is required")
    private Long classId;

    private List<Long> preferredSubjectIds;

    private String message;
}
