package example.bfe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/bankdb"; 
        String user = "root"; 
        String password = System.getenv("MYSQL_DB_PASSWORD");

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println(" JDBC Connection Successful!");

            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(" JDBC Connection Failed!");
            e.printStackTrace();
        }
    }
}