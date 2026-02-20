package com.example.studentms.web;

import com.example.studentms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
  private final ReportService reportService;

  @GetMapping("/attendance/excel/{courseId}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<byte[]> exportExcel(@PathVariable Long courseId) throws Exception {
    byte[] bytes = reportService.exportAttendanceExcel(courseId);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.xlsx")
        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(bytes);
  }

  @GetMapping("/attendance/pdf/{courseId}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<byte[]> exportPdf(@PathVariable Long courseId) throws Exception {
    byte[] bytes = reportService.exportAttendancePdf(courseId);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(bytes);
  }
}
