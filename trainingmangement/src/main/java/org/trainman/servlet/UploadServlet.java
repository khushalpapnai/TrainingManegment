package org.trainman.servlet;

import org.trainman.dao.EmployeeDao;
import org.trainman.model.Employee;
import org.trainman.util.ExcelParser;
import org.trainman.util.ExcelParser.ParseResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class UploadServlet extends HttpServlet {

    private static final int BATCH_SIZE = 500;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            req.setAttribute("result", buildResult(0, java.util.Arrays.asList("No file uploaded")));
            req.getRequestDispatcher("/upload.jsp").forward(req, resp);
            return;
        }

        String fileName = getFileName(filePart);
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            req.setAttribute("result", buildResult(0, java.util.Arrays.asList("Only .xlsx files are accepted")));
            req.getRequestDispatcher("/upload.jsp").forward(req, resp);
            return;
        }

        try (InputStream in = filePart.getInputStream()) {
            ParseResult parseResult = ExcelParser.parseXlsx(in);
            List<Employee> employees = parseResult.employees;

            if (!parseResult.errors.isEmpty() && employees.isEmpty()) {
                req.setAttribute("result", buildResult(0, parseResult.errors));
                req.getRequestDispatcher("/upload.jsp").forward(req, resp);
                return;
            }

            EmployeeDao dao = new EmployeeDao();
            int inserted = 0;
            try {
                inserted = dao.batchInsert(getServletContext(), employees, BATCH_SIZE);
            } catch (Exception ex) {
                parseResult.errors.add("DB error: " + ex.getMessage());
            }

            req.setAttribute("result", buildResult(inserted, parseResult.errors));
            req.getRequestDispatcher("/upload.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("result", buildResult(0, java.util.Arrays.asList("Processing error: " + e.getMessage())));
            req.getRequestDispatcher("/upload.jsp").forward(req, resp);
        }
    }

    private String getFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd == null) return null;
        for (String seg : cd.split(";")) {
            seg = seg.trim();
            if (seg.startsWith("filename")) {
                return seg.substring(seg.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private Object buildResult(int inserted, java.util.List<String> errors) {
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        m.put("inserted", inserted);
        m.put("errors", errors);
        return m;
    }
}
