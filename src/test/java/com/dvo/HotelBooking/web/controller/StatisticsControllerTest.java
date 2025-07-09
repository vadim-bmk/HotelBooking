package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.configuration.SecurityConfiguration;
import com.dvo.HotelBooking.security.UserDetailsServiceImpl;
import com.dvo.HotelBooking.service.StatisticsService;
import com.dvo.HotelBooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@Import({SecurityConfiguration.class, UserDetailsServiceImpl.class})
public class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testDownloadCsv() throws Exception {
        File tempFile = Files.createTempFile("test-statistics", ".csv").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Type,User ID,Check in Date,Check out Date\n");
            writer.write("Booking,1,2025-07-01,2025-07-05\n");
        }

        when(statisticsService.exportStatisticsToCsv()).thenReturn(tempFile.getAbsolutePath());

        mockMvc.perform(get("/api/statistics/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tempFile.getAbsolutePath()))
                .andExpect(content().contentType("application/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Booking,1,2025-07-01")));

        tempFile.deleteOnExit();
    }
}
