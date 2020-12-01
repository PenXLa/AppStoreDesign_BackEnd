package DAO.publisher;

import kernel.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AppInfoModifier {
    public static class PermissionDeniedException extends Exception{}
    public static void setEnabled(String publisher, String appid, boolean enabled) throws SQLException, ClassNotFoundException, PermissionDeniedException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("update Applications set Active=? where AppID=? and Publisher=?");
        ) {
            stat.setBoolean(1, enabled);
            stat.setNString(2, appid);
            stat.setNString(3, publisher);
            int lines = stat.executeUpdate();
            if (lines == 0) throw new PermissionDeniedException();
        }
    }
}
