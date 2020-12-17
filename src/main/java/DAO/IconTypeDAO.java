package DAO;

import kernel.Account;
import utils.SelectQuery;
import utils.UpdateQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IconTypeDAO {
    public static void changeIconType(Account user, String appid, String type) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = new UpdateQuery().update("Applications").set("IconType=?", type)
                    .where("AppID=?", appid).where("Publisher=?", user.getPublisher()).toStatement(con);
        ) {
            stat.executeUpdate();
        }
    }
//
//    public static String getIconType(Account user, String appid) throws SQLException, ClassNotFoundException {
//        try (
//            Connection con = Utils.connectDB("AppStoreDesign");
//            PreparedStatement stat = new SelectQuery().select("IconType").from("Applications")
//                    .where("AppID=?", appid).where("Publisher=?", user.getPublisher()).toStatement(con);
//            ResultSet res = stat.executeQuery();
//        ) {
//            res.next();
//            return res.getString(1);
//        }
//    }
}
