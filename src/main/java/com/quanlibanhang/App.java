
package com.quanlibanhang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {

        String url = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=quanlibanhang;"
                + "encrypt=true;"
                + "trustServerCertificate=true;";

        String user = "java_app";
        String password = System.getenv("DB_PASSWORD");

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Ket noi SQL Server thanh cong!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Ket noi that bai!");
            System.out.println(e.getMessage());
        }
    }
}
