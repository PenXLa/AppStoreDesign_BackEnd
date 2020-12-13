package DAO;

import kernel.Account;
import utils.SelectQuery;
import utils.Utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ApplyReturnDAO {
    public static class AlreadyAppliedException extends Exception{}
    public static class WrongOrderIDException extends Exception{}
    public static void returnApp(Account user, String appid, String reaosn) throws SQLException, ClassNotFoundException, AlreadyAppliedException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            CallableStatement stat = con.prepareCall("{ call ApplyReturnApp(?,?,?) }");
        ) {
            stat.setNString(1, appid);
            stat.setNString(2, user.getUid());
            stat.setNString(3, reaosn);
            stat.execute();
        } catch (SQLException e) {
            if (e.getErrorCode()==50004)  //已经申请过了
                throw new AlreadyAppliedException();
            else throw e;
        }
    }

    public static void returnOrder(Account user, String oid, String reason) throws ClassNotFoundException, AlreadyAppliedException, WrongOrderIDException, SQLException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            CallableStatement stat = con.prepareCall("{ call ApplyReturnOrder(?,?,?) }");
        ) {
            stat.setNString(1, oid);
            stat.setNString(2, user.getUid());
            stat.setNString(3, reason);
            stat.execute();
        } catch (SQLException e) {
            if (e.getErrorCode()==50004)  //已经申请过了
                throw new AlreadyAppliedException();
            else if (e.getErrorCode()==50005) {
                throw new WrongOrderIDException();
            } else throw e;
        }
    }
}
