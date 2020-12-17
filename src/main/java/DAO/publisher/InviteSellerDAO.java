package DAO.publisher;

import kernel.Account;
import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InviteSellerDAO {
    public static String inviteSeller(Account user) throws SQLException, ClassNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement newid = con.prepareStatement("select newid()");
                PreparedStatement stat = con.prepareStatement("insert into InvitationCodes(Code, Creater, URole) Values(?,?,'seller')");
                ResultSet res = newid.executeQuery();
        ) {
            res.next();
            String id = res.getString(1);
            stat.setNString(2, user.getUid());
            stat.setString(1, id);
            stat.executeUpdate();
            return id;
        }
    }
}
