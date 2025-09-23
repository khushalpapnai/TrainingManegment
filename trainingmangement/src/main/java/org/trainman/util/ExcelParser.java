package org.trainman.util;

import org.trainman.model.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelParser {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Set<String> ALLOWED_STATUS = new HashSet<>(Arrays.asList(
            "Allocated", "Under Traning", "Resizined", "Terminated", "Temp Allocation", "wating for allocation"
    ));

    public static class ParseResult {
        public final List<Employee> employees = new ArrayList<>();
        public final List<String> errors = new ArrayList<>();
    }

    public static ParseResult parseXlsx(InputStream in) throws Exception {
        ParseResult result = new ParseResult();
        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                result.errors.add("Workbook has no sheets");
                return result;
            }

            Map<String, Integer> colIndex = mapHeader(sheet.getRow(sheet.getFirstRowNum()));
            if (colIndex.isEmpty()) {
                result.errors.add("Header row not found or empty");
                return result;
            }

            int rowNum = 0;
            for (Row row : sheet) {
                if (row.getRowNum() == sheet.getFirstRowNum()) continue;
                rowNum = row.getRowNum() + 1; // human-readable

                String empid = getCellString(row, colIndex.get("empid"));
                if (empid == null || empid.trim().isEmpty()) {
                    result.errors.add("Row " + rowNum + ": empid is empty");
                    continue;
                }
                Employee e = new Employee();
                e.setEmpId(empid.trim());
                e.setName(getCellString(row, colIndex.get("name")));
                e.setGender(getCellString(row, colIndex.get("gender")));
                e.setNsbtBatchNo(getCellString(row, colIndex.get("nsbt batchno")));
                String status = getCellString(row, colIndex.get("status"));
                if (status == null) status = "";
                status = status.trim();
                if (!ALLOWED_STATUS.contains(status)) {
                    result.errors.add("Row " + rowNum + ": invalid status '" + status + "'");
                    continue;
                }
                e.setStatus(status);

                try {
                    e.setDoj(parseDateCell(row, colIndex.get("doj")));
                    e.setResignationDate(parseDateCell(row, colIndex.get("resiznation date")));
                    e.setReleasedDate(parseDateCell(row, colIndex.get("released date")));
                } catch (Exception ex) {
                    result.errors.add("Row " + rowNum + ": date parse error - " + ex.getMessage());
                    continue;
                }

                e.setGrade(getCellString(row, colIndex.get("grade")));
                e.setBu(getCellString(row, colIndex.get("bu")));
                e.setMprNo(getCellString(row, colIndex.get("mpr no")));
                e.setIoName(getCellString(row, colIndex.get("io name")));
                result.employees.add(e);
            }
        }
        return result;
    }

    private static Map<String, Integer> mapHeader(Row header) {
        Map<String, Integer> idx = new HashMap<>();
        if (header == null) return idx;
        for (Cell c : header) {
            String v = c.getStringCellValue();
            if (v == null) continue;
            String key = v.trim().toLowerCase();
            idx.put(key, c.getColumnIndex());
        }
        // Accept common variants by mapping normalized keys
        Map<String, Integer> normalized = new HashMap<>();
        for (Map.Entry<String, Integer> e : idx.entrySet()) {
            String k = e.getKey();
            if (k.contains("empid") || k.contains("emp id")) normalized.put("empid", e.getValue());
            if (k.equals("name")) normalized.put("name", e.getValue());
            if (k.contains("gender")) normalized.put("gender", e.getValue());
            if (k.contains("doj")) normalized.put("doj", e.getValue());
            if (k.contains("nsbt") && k.contains("batch")) normalized.put("nsbt batchno", e.getValue());
            if (k.contains("status")) normalized.put("status", e.getValue());
            if (k.contains("resiz") || k.contains("resign")) normalized.put("resiznation date", e.getValue());
            if (k.contains("released")) normalized.put("released date", e.getValue());
            if (k.contains("grade")) normalized.put("grade", e.getValue());
            if (k.equals("bu")) normalized.put("bu", e.getValue());
            if (k.contains("mpr")) normalized.put("mpr no", e.getValue());
            if (k.toLowerCase().contains("io") || k.toLowerCase().contains("io name")) normalized.put("io name", e.getValue());
        }
        return normalized;
    }

    private static String getCellString(Row row, Integer idx) {
        if (idx == null) return null;
        Cell c = row.getCell(idx);
        if (c == null) return null;
        if (c.getCellType() == CellType.STRING) return c.getStringCellValue();
        if (c.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(c)) {
                LocalDate d = c.getLocalDateTimeCellValue().toLocalDate();
                return d.format(DTF);
            } else {
                double d = c.getNumericCellValue();
                long l = (long) d;
                if (Math.abs(d - l) < 0.0001) return String.valueOf(l);
                return String.valueOf(d);
            }
        }
        if (c.getCellType() == CellType.BLANK) return null;
        return c.toString();
    }

    private static LocalDate parseDateCell(Row row, Integer idx) {
        if (idx == null) return null;
        Cell c = row.getCell(idx);
        if (c == null) return null;
        if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
            return c.getLocalDateTimeCellValue().toLocalDate();
        } else {
            String s = c.toString().trim();
            if (s.isEmpty()) return null;
            return LocalDate.parse(s, DTF);
        }
    }
}
