package DAO.publisher;

import VO.SellerVO;
import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SellerDAO {
    public static SellerVO[] getSellers(String pubid) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = new SelectQuery().select("S.*, U.Name As Name, Manager").
                                        from("Sellers S INNER JOIN Users U ON S.UID=U.UID INNER JOIN Publishers P ON P.PubID=S.Publisher")
                                        .where("Publisher=?",pubid)
                                        .where("Manager!=S.UID").toStatement(con);
            ResultSet res = stat.executeQuery();
        ) {
            ArrayList<SellerVO> sellers = new ArrayList<>();
            while(res.next()) {
                SellerVO seller = new SellerVO();
                seller.uid = res.getString("UID");
                seller.name = res.getNString("Name");
                sellers.add(seller);
            }
            return sellers.toArray(new SellerVO[0]);
        }
    }

    public static void removeSeller(String uid) throws SQLException, ClassNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = con.prepareStatement("delete from Sellers where UID=?");
        ) {
            stat.setString(1, uid);
            stat.executeUpdate();
        }
    }
}
