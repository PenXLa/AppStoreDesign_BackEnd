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


    public static Pair<Integer, OrderVO[]> getAllOrders(Account user, int page, int pageSize) throws SQLException, ClassNotFoundException {
        return queryOrders(user, null, null, page, pageSize);
    }
    public static Pair<Integer, OrderVO[]> getAppOrders(Account user, String appid, int page, int pageSize) throws SQLException, ClassNotFoundException {
        return queryOrders(user, appid, null, page, pageSize);
    }
    public static Pair<Integer, OrderVO[]> getOrder(Account user, String oid) throws SQLException, ClassNotFoundException {
        return queryOrders(user, null, oid, 1, 1);
    }


    //如果appid不为null，就查询和appid有关的order
    //如果oid不为null，就单查询这个order
    //如果都不为null，就两个条件都满足（不常用）
    //如果都为null，就查询所有order
    //查询和某个app有关的order，如果appid为null，则
    //返回结果集 和 分页前结果总数
    private static Pair<Integer, OrderVO[]> queryOrders(Account user, String appid, String oid, int page, int pageSize) throws SQLException, ClassNotFoundException {
        String subsql = "select O.*, AP.Name as PlanName, A.Name as AppName, IsMainPlan from OrdersWithStatus O, Applications A, AppPlans AP where O.AppID=AP.AppID and O.AppID=A.AppID and O.PlanID=AP.PlanID " +
                "UNION select O.*, NULL as PlanName, NULL as AppName, NULL from OrdersWithStatus O where AppID is null";

        SelectQuery sql = new SelectQuery();
        sql.select("*").from(String.format("(%s) as tmp", subsql));
        sql.orderBy("date desc").paginate(page, pageSize);
        sql.where("UID=?", user.getUid());
        if (appid != null) sql.where("AppID=?", appid);
        if (oid != null) sql.where("OID=?", oid);
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = sql.toStatement(con);
            PreparedStatement countStat = sql.toCountStatement(con);
            ResultSet countRes = countStat.executeQuery();
            ResultSet res = stat.executeQuery()
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
                order.status = res.getShort("Stat");

                orders.add(order);
            }
            countRes.next();
            return new ImmutablePair<>(countRes.getInt(1), orders.toArray(new OrderVO[0]));
        }
    }


}
