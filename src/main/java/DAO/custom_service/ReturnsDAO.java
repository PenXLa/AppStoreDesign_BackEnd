package DAO.custom_service;

import VO.ReturnVO;
import kernel.Account;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import utils.SelectQuery;
import utils.UpdateQuery;
import utils.Utils;

import java.sql.*;
import java.util.ArrayList;

public class ReturnsDAO {
    public static Pair<Integer, ReturnVO[]> getReturns(Account user, int page, int pageSize) throws SQLException, ClassNotFoundException {
        SelectQuery sql = new SelectQuery();
        sql.select("R.*, A.Name As AppName, isMainPlan, U.Name AS UName, AP.Name As PlanName")
                .from("ReturnInfos R INNER JOIN Applications A ON R.AppID=A.AppID INNER JOIN AppPlans AP ON R.PlanID=AP.PlanID INNER JOIN Users U ON R.UID=U.UID")
                .where("Stage=1").where("Handler=?", user.getUid())
                .orderBy("RDate asc").paginate(page, pageSize);
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = sql.toStatement(con);
                ResultSet res = stat.executeQuery();
        ) {
            ArrayList<ReturnVO> applies = new ArrayList<>();
            while (res.next()) {
                ReturnVO apply = new ReturnVO();
                apply.rid = res.getString("RID");
                apply.appid = res.getString("AppID");
                apply.appname = res.getNString("AppName");
                apply.applyDate = res.getTimestamp("RDate");
                apply.buyDate = res.getTimestamp("ODate");
                apply.description = res.getNString("Description");
                apply.oid = res.getString("OID");
                apply.isMainPlan = res.getBoolean("isMainPlan");
                apply.price = (long)(res.getDouble("Price")*1000);
                apply.uid = res.getString("UID");
                apply.userName = res.getNString("UName");
                apply.planname = res.getNString("PlanName");
                apply.planid = res.getString("PlanID");
                apply.reason = res.getNString("Reason");
                applies.add(apply);
            }
            return new ImmutablePair<>(sql.getResultTotalSize(con), applies.toArray(new ReturnVO[0]));
        }
    }

    public static void handleApply(String rid, boolean accept, String reason) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            CallableStatement call = con.prepareCall("{ call HandleReturnApply(?, ?, ?) }")
        ) {
            call.setString("rid", rid);
            call.setBoolean("accept", accept);
            call.setNString("reason", reason);
            call.execute();
        }
    }
}
