package dal;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {

    protected Connection connection;

    public DBContext() {
        try {
            String user = "sa";
            String pass = "123";

            String url = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=LaMerHotel;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            System.out.println("DBContext error: " + e.getMessage());
        }
    }
}