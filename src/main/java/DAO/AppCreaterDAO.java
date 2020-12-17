package DAO;

import kernel.Account;
import utils.Utils;

import java.sql.*;

public class AppCreaterDAO {
    public static String createApp(Account user) throws SQLException, ClassNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                CallableStatement call = con.prepareCall("{ call CreateEmptyApp(?, ?) }")
        ) {
            call.setString("pubid", user.getPublisher());
            call.registerOutParameter("appid", Types.VARCHAR);
            call.execute();
            return call.getString("appid");
        }
    }


    public static String createPlan(Account user, String appid) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            CallableStatement call = con.prepareCall("{ call CreateEmptyPlan(?, ?, ?) }")
        ) {
            call.setString("pubid", user.getPublisher());
            call.setString("appid", appid);
            call.registerOutParameter("planid", Types.VARCHAR);
            call.execute();
            return call.getString("planid");
        }
    }
}
