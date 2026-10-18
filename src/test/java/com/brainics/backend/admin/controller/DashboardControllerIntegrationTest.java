package com.brainics.backend.admin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDashboardStatsSuccess() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("enquiries")))
                .andExpect(jsonPath("$", hasKey("users")))
                .andExpect(jsonPath("$", hasKey("teachers")))
                .andExpect(jsonPath("$", hasKey("academic")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEnquiryStatsSuccess() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/enquiry-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEnquiries").isNumber())
                .andExpect(jsonPath("$.conversionRate").isNumber());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserStatsSuccess() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/user-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").isNumber())
                .andExpect(jsonPath("$.adminCount").isNumber())
                .andExpect(jsonPath("$.teacherCount").isNumber());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTeacherStatsSuccess() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/teacher-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTeachers").isNumber());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetActionCountsSuccess() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/action-counts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStatsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetStatsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
