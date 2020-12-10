package DAO;

import kernel.Account;
import utils.Utils;

import java.sql.*;

public class DoCommentDAO {
    //由于数据库设置了触发器，insert可以覆盖旧的，所以这里就不必区分是insert还是update了
    public static void comment(Account user, String appid, String content, double rating) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement(
                    "insert into CommentsOn(AppID, UID, Content, Rating, Date) VALUES(?,?,?,?,?)");
        ) {
            stat.setNString(1, appid);
            stat.setNString(2, user.getEmail());
            stat.setNString(3, content);
            stat.setDouble(4, rating);
            stat.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stat.executeUpdate();
        }
    }
}
