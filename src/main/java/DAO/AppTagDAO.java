package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppTagDAO {
    public static String getAppTags(String appid, Connection con) throws SQLException {
        try (PreparedStatement tagQuery = con.prepareStatement("select Tag from AppTags where AppID = ?")) {
            tagQuery.setNString(1, appid);
            try (ResultSet tagRes = tagQuery.executeQuery()) {
                StringBuffer sb = new StringBuffer();
                while(tagRes.next())
                    sb.append(tagRes.getNString(1) + "|");
                sb.deleteCharAt(sb.length()-1);
                return sb.toString();
            }
        }
    }
}
