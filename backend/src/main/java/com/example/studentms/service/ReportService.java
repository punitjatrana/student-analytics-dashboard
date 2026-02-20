package com.example.studentms.service;

import com.example.studentms.domain.Attendance;
import com.example.studentms.domain.Course;
import com.example.studentms.repository.AttendanceRepository;
import com.example.studentms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final CourseRepository courseRepository;
  private final AttendanceRepository attendanceRepository;

  public byte[] exportAttendanceExcel(Long courseId) throws Exception {
    Course course = courseRepository.findById(courseId).orElseThrow();
    List<Attendance> records = attendanceRepository.findByCourse(course);
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Attendance");
      int r = 0;
      Row header = sheet.createRow(r++);
      header.createCell(0).setCellValue("StudentId");
      header.createCell(1).setCellValue("Date");
      header.createCell(2).setCellValue("Present");
      for (Attendance a : records) {
        Row row = sheet.createRow(r++);
        row.createCell(0).setCellValue(a.getStudent().getId());
        row.createCell(1).setCellValue(a.getSessionDate().toString());
        row.createCell(2).setCellValue(a.isPresent());
      }
      workbook.write(out);
      return out.toByteArray();
    }
  }

  public byte[] exportAttendancePdf(Long courseId) throws Exception {
    Course course = courseRepository.findById(courseId).orElseThrow();
    List<Attendance> records = attendanceRepository.findByCourse(course);
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Document doc = new Document();
      PdfWriter.getInstance(doc, out);
      doc.open();
      doc.add(new Paragraph("Attendance Report - " + course.getCode() + " - " + course.getTitle()));
      for (Attendance a : records) {
        doc.add(new Paragraph(
            "Student " + a.getStudent().getId() + " | " + a.getSessionDate() + " | present=" + a.isPresent()));
      }
      doc.close();
      return out.toByteArray();
    }
  }
}
