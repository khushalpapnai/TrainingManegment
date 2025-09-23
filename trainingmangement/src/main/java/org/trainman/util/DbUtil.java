package org.trainman.util;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {

    public static Connection getConnection(ServletContext context) throws SQLException {
        String url = context.getInitParameter("db.url");
        String user = context.getInitParameter("db.username");
        String pass = context.getInitParameter("db.password");
        return DriverManager.getConnection(url, user, pass);
    }
}

