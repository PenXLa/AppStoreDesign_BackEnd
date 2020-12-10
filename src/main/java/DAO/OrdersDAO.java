package DAO;

import VO.OrderVO;
import kernel.Account;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrdersDAO {
    //返回<总数，结果集>元组
    public static Pair<Integer, OrderVO[]> getOrders(Account user, int page, int pageSize) throws SQLException, ClassNotFoundException {
        String sql = "select O.*, AP.Name as PlanName, A.Name as AppName, IsMainPlan from OrdersWithReturns O, Applications A, AppPlans AP where UID=? and O.AppID=AP.AppID and O.AppID=A.AppID and O.PlanID=AP.PlanID " +
                "UNION select O.*, NULL as PlanName, NULL as AppName, NULL from OrdersWithReturns O where AppID is null and UID=?";
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("select * from (" + sql +") as tmp order by date desc " + SelectQuery.paginateTSQL(page, pageSize));
            PreparedStatement countStat = con.prepareStatement("select count(*) from (" + sql +") as tmp")
        ) {
            stat.setNString(1, user.getEmail());
            stat.setNString(2, user.getEmail());
            countStat.setNString(1, user.getEmail());
            countStat.setNString(2, user.getEmail());
            try (
                ResultSet res = stat.executeQuery();
                ResultSet countRes = countStat.executeQuery()
            ) {
                ArrayList<OrderVO> orders = new ArrayList<>();
                while(res.next()) {
                    OrderVO order = new OrderVO();
                    order.appid = res.getNString("AppID");
                    order.appname = res.getNString("AppName");
                    order.planid = res.getNString("PlanID");
                    order.planname = res.getNString("PlanName");
                    order.date = res.getTimestamp("Date");
                    order.description = res.getNString("Description");
                    order.price = (long)(res.getDouble("Price")*1000);
                    order.OID = res.getString("OID");
                    order.isMainPlan = res.getBoolean("IsMainPlan");
                    order.returned = res.getBoolean("Returned");

                    orders.add(order);
                }
                countRes.next();
                return new ImmutablePair<>(countRes.getInt(1), orders.toArray(new OrderVO[0]));
            }
        }

    }
}
