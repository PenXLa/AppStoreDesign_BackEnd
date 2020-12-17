package DAO.publisher;

import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IncomeDAO {
    public static double getIncome(String pubid) throws SQLException, ClassNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = new SelectQuery().select("*").from("Publishers").where("PubID=?", pubid).toStatement(con);
                ResultSet res = stat.executeQuery();
        ) {
            res.next();
            return res.getDouble("Income");
        }
    }
}
