package DAO;

import kernel.Account;
import utils.Utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class AppRemoverDAO {
    public static class PermissionDeniedException extends Exception{};
    public static class StillHasUsersException extends Exception{};
    public static void removeApp(Account user, String appid) throws SQLException, ClassNotFoundException, PermissionDeniedException, StillHasUsersException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                CallableStatement call = con.prepareCall("{ call RemoveApp(?, ?) }")
        ) {
            call.setString("pubid", user.getPublisher());
            call.setString("appid", appid);
            call.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 50001) throw new PermissionDeniedException();
            else if (e.getErrorCode() == 50007) throw new StillHasUsersException();
            else throw e;
        }
    }

    public static void removePlan(Account user, String planid) throws SQLException, ClassNotFoundException, PermissionDeniedException, StillHasUsersException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                CallableStatement call = con.prepareCall("{ call RemovePlan(?, ?) }")
        ) {
            call.setString("pubid", user.getPublisher());
            call.setString("planid", planid);
            call.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 50001) throw new PermissionDeniedException();
            else if (e.getErrorCode() == 50007) throw new StillHasUsersException();
            else throw e;
        }
    }
}
