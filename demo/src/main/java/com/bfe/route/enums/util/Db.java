package com.bfe.route.enums.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    
    private static final String URL = "jdbc:mysql://localhost:3306/bank_simulator"; 
    private static final String USER = "root";
    private static final String PASSWORD = "Diamond#Me100%";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
