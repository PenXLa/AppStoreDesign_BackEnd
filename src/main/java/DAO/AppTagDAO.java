package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppTagDAO {
    public static String[] getAppTags(String appid, Connection con) throws SQLException {
        try (PreparedStatement tagQuery = con.prepareStatement("select Tag from AppTags where AppID = ?")) {
            tagQuery.setNString(1, appid);
            try (ResultSet tagRes = tagQuery.executeQuery()) {
                ArrayList<String> list = new ArrayList<>();
                while(tagRes.next())
                    list.add(tagRes.getNString(1));
                return list.toArray(new String[0]);
            }
        }
    }
}
