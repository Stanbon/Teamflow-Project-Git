import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/teamflow";
    private static final String USER = "root";
    private static final String PASSWORD = "vI@7pZbE@2fFt4I";

    public static Connection connectDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        return connection;
    }

    public static void main(String[] args) {
        try {
            Connection connection = connectDatabase();
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

}
