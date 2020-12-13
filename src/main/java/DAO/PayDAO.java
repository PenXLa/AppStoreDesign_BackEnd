package DAO;

import kernel.Account;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PayDAO {
    public static void pay(Account user, long amount) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("INSERT INTO Orders(OID, UID, AppID, PlanID, Price, Date, Description)" +
                    "VALUES (newID(), ?, NULL, NULL, ?, getdate(), N'账户充值')")
        ) {
            try {
                con.setAutoCommit(false); //开始事务
                stat.setNString(1, user.getUid());
                stat.setDouble(2, -amount/1000.0);

                stat.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback(); //出错，回退
                throw e;
            }
        }
    }
}
