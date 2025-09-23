package org.trainman.dao;

import org.trainman.model.Employee;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

import org.trainman.util.DbUtil;

public class EmployeeDao {

    private static final String INSERT_SQL =
            "INSERT INTO employee(empid, name, gender, doj, nsbt_batch_no, status, resignation_date, released_date, grade, bu, mpr_no, io_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public int batchInsert(ServletContext ctx, List<Employee> list, int batchSize) throws SQLException {
        int inserted = 0;
        try (Connection conn = DbUtil.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            conn.setAutoCommit(false);
            int count = 0;
            for (Employee e : list) {
                ps.setString(1, e.getEmpId());
                ps.setString(2, e.getName());
                ps.setString(3, e.getGender());
                ps.setDate(4, e.getDoj() == null ? null : Date.valueOf(e.getDoj()));
                ps.setString(5, e.getNsbtBatchNo());
                ps.setString(6, e.getStatus());
                ps.setDate(7, e.getResignationDate() == null ? null : Date.valueOf(e.getResignationDate()));
                ps.setDate(8, e.getReleasedDate() == null ? null : Date.valueOf(e.getReleasedDate()));
                ps.setString(9, e.getGrade());
                ps.setString(10, e.getBu());
                ps.setString(11, e.getMprNo());
                ps.setString(12, e.getIoName());
                ps.addBatch();
                count++;
                if (count % batchSize == 0) {
                    int[] res = ps.executeBatch();
                    conn.commit();
                    inserted += sumInserted(res);
                }
            }
            if (count % batchSize != 0) {
                int[] res = ps.executeBatch();
                conn.commit();
                inserted += sumInserted(res);
            }
        }
        return inserted;
    }

    private int sumInserted(int[] arr) {
        int s = 0;
        for (int i : arr) {
            if (i == PreparedStatement.SUCCESS_NO_INFO || i > 0) s++;
        }
        return s;
    }
}

