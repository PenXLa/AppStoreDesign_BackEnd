package DAO;

import kernel.Account;
import utils.Utils;

import java.sql.*;

public class BuyDAO {
    public static class InsufficientBalanceException extends Exception{}
    public static class InvalidProductException extends Exception{} //购买不存在或未上架的App
    public static class AlreadyOwnException extends Exception{}
    //返回订单号
    public static String buy(Account user, String appid, String planid) throws SQLException, ClassNotFoundException, AlreadyOwnException, InsufficientBalanceException, InvalidProductException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            CallableStatement call = con.prepareCall("{ call BuyPlan(?, ?, ?, ?) }")
        ) {
            call.setNString("uid", user.getEmail());
            call.setNString("appid", appid);
            call.setNString("planid", planid);
            call.registerOutParameter("oid", Types.VARCHAR);
            call.execute();
            return call.getString("oid");
        } catch (SQLException e) {
            if (e.getErrorCode() == 50001) {
                throw new InvalidProductException();
            } else if (e.getErrorCode() == 50002) {
                throw new InsufficientBalanceException();
            } else if (e.getErrorCode() == 50003) {
                throw new AlreadyOwnException();
            } else throw e;
        }
    }

}
