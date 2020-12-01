package kernel;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Utils {
    public static Connection connectDB(String database) throws ClassNotFoundException, SQLException {
        final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                url = "jdbc:sqlserver://impxl.cn:1433;databasename=",
                pwd = "tyRuZf18",
                user = "AppStoreWeb";
        Class.forName(driver);
        Connection con = DriverManager.getConnection(url+database, user, pwd);
        return con;
    }


}
