package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=event_booking_system;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        }
        catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void testConnection() {
        System.out.println("🔗 Testing connection...");
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("🎉 SUCCESS! Connected to SQL Server");
            }
        }
        catch (Exception e) {
            System.err.println("💥 FAILED: " + e.getMessage());
        }
    }
}