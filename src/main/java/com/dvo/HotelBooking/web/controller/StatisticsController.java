package com.dvo.HotelBooking.web.controller;

import com.dvo.HotelBooking.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/download")
    @Operation(
            summary = "Download statistics",
            description = "Download statistics as CSV file",
            tags = {"statistics"}
    )
    public ResponseEntity<FileSystemResource> downloadCsv() {
        String csvFile = statisticsService.exportStatisticsToCsv();
        FileSystemResource resource = new FileSystemResource(csvFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFile)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }
}
