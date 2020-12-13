package DAO;

import VO.UserAppVO;
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
import java.util.HashMap;

public class MyAppsDAO {
    public static Pair<Integer, UserAppVO[]> userApps(Account user, int page, int pageSize) throws SQLException, ClassNotFoundException {
        SelectQuery sql = new SelectQuery();
        sql.select("P.*, LastUpdate, IconType, Version").from("PossessesEx P INNER JOIN Applications A ON P.AppID=A.AppID");
        sql.where("UID=?", user.getUid());
        sql.orderBy("AppName, PlanName").paginate(page, pageSize);
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = sql.toStatement(con);
                PreparedStatement countStat = sql.toCountStatement(con);
                ResultSet res = stat.executeQuery();
                ResultSet countRes = countStat.executeQuery()
        ) {
            ArrayList<UserAppVO> apps = new ArrayList<>();
            HashMap<String, ArrayList<UserAppVO.SubscribeItem>> subscribes = new HashMap<>();//搜到的结果是MainPlan和订阅项目掺一块的，所以用Map分类一下
            while(res.next()) { //遍历App
                if (res.getBoolean("IsMainPlan")) { //是MainPlan
                    UserAppVO item = new UserAppVO();
                    item.appid = res.getNString("AppID");
                    item.name = res.getNString("AppName");
                    item.icon = Utils.getAppIconURL(item.appid, res.getString("IconType"));
                    item.tags = AppTagDAO.getAppTags(item.appid, con);//获取Tag
                    item.lastUpdate = res.getTimestamp("LastUpdate");
                    item.version = res.getNString("Version");
                    apps.add(item);
                } else {
                    UserAppVO.SubscribeItem item = new UserAppVO.SubscribeItem();
                    item.expireDate = res.getTimestamp("ExpireDate");
                    item.explanation = res.getNString("Explanation");
                    item.name = res.getNString("PlanName");
                    item.planid = res.getNString("PlanID");
                    String appid = res.getNString("AppID");
                    if (!subscribes.containsKey(appid)) subscribes.put(appid, new ArrayList<>());
                    subscribes.get(appid).add(item);
                }
            }
            UserAppVO[] appArr = apps.toArray(new UserAppVO[0]);
            for (UserAppVO item : appArr)
                if (subscribes.containsKey(item.appid))
                    item.subscribeItems = subscribes.get(item.appid).toArray(new UserAppVO.SubscribeItem[0]);

            countRes.next();
            return new ImmutablePair<>(countRes.getInt(1), appArr);
        }
    }
}
