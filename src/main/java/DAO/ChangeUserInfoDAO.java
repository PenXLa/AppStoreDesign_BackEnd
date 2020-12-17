package DAO;

import kernel.Account;
import kernel.AccountUtils;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangeUserInfoDAO {
    public static void changeUserName(Account user, String newName) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("update Users set Name=? where UID=?");
        ) {
            stat.setNString(1, newName);
            stat.setNString(2, user.getUid());
            stat.executeUpdate();
        }

    }

    public static void changePasswd(String email, String pwd) throws SQLException, ClassNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = con.prepareStatement("update Users set PassHash=? where Email=?");
        ) {
            stat.setNString(1, AccountUtils.calcHash(pwd));
            stat.setNString(2, email);
            stat.executeUpdate();
        }
    }
}
