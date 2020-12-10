package DAO;

import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReturnOrder {
    public static void returnOrder(String oid) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("insert into Returns(OID, Date) Values(?,getdate())");
        ) {
            stat.setNString(1, oid);
            stat.executeUpdate();
        }
    }
}
