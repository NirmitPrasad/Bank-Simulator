package example.bfe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // Run JDBCTest first
        JDBCTest.main(args);

        logger.info("Application started");

        String url = "jdbc:mysql://localhost:3306/bank_simulator"; 
        String user = "root"; 
        String password = System.getenv("MYSQL_DB_PASSWORD"); 

        try {
            logger.info("Connecting to MySQL database...");
            Connection con = DriverManager.getConnection(url, user, password);
            logger.info("Connection established.");

            Statement stmt = con.createStatement();
            logger.info("Statement created. Executing query: SELECT VERSION()");
            ResultSet rs = stmt.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                logger.info("Connected to MySQL version: {}", rs.getString(1));
                System.out.println("Connected to MySQL version: " + rs.getString(1));
            } else {
                logger.warn("No result returned from SELECT VERSION()");
            }
            rs.close();

            // List tables in the database
            logger.info("Listing tables in the database...");
            ResultSet tablesRs = stmt.executeQuery("SHOW TABLES");
            System.out.println("Tables in the database:");
            while (tablesRs.next()) {
                System.out.println(tablesRs.getString(1));
            }
            tablesRs.close();

            // View contents of a table (example: 'customer_details')
            String tableName = "customer_details"; 
            logger.info("Selecting all rows from table: {}", tableName);
            ResultSet dataRs = stmt.executeQuery("SELECT * FROM " + tableName);
            System.out.println("Contents of table '" + tableName + "':");
            int columns = dataRs.getMetaData().getColumnCount();
            while (dataRs.next()) {
                for (int i = 1; i <= columns; i++) {
                    System.out.print(dataRs.getString(i) + "\t");
                }
                System.out.println();
            }
            dataRs.close();

            stmt.close();
            con.close();
            logger.info("Resources closed. Application finished.");
        } catch (Exception e) {
            logger.error("Error connecting to MySQL: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
