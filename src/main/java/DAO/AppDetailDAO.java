package DAO;

import VO.AppDetailVO;
import kernel.Account;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppDetailDAO {
    public static class AppNotFoundException extends Exception { }
    //如果是seller查询，就可以查到自己的app的active状况
    public static AppDetailVO getAppDetail(String appid, Account user) throws SQLException, ClassNotFoundException, AppNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("select * from AppDetails where AppID = ?")
        ) {
            stat.setNString(1, appid);
            try (ResultSet res = stat.executeQuery()) {
                if (res.next()) {
                    AppDetailVO detail = new AppDetailVO();
                    detail.CPU = res.getNString("Req_CPU");
                    detail.publisher = res.getNString("PubName");
                    detail.GPU = res.getNString("Req_GPU");
                    detail.OS = res.getNString("Req_OS");
                    detail.req_other = res.getNString("Req_Other");
                    detail.hardDisk = res.getLong("Req_HardDisk");
                    detail.RAM = res.getLong("Req_RAM");
                    detail.iconType = res.getString("iconType");
                    detail.id = appid;
                    detail.introduction = res.getNString("introduction");
                    detail.lastUpdate = res.getDate("LastUpdate");
                    detail.name = res.getNString("Name");
                    detail.rating = res.getDouble("Rating");
                    detail.site = res.getNString("OfficalSite");
                    detail.size = res.getLong("Size");
                    detail.version = res.getNString("Version");
                    detail.tags = AppTagDAO.getAppTags(appid, con);

                    var plans = getAppPlans(con, appid, user);
                    detail.subscribePlans = new AppDetailVO.AppPlan[plans.size()-1]; //假设了有且只有一个MainPlan
                    int inx = 0;
                    for (var plan : plans)
                        if (plan.isMain) detail.mainPlan = plan;
                        else detail.subscribePlans[inx++] = plan;

                    //验证publisher权限，如果通过，则提供更多信息
                    if (user != null && "seller".equals(user.getRole()) && user.getPublisher().equals(res.getNString("Publisher"))) {
                        detail.active = res.getBoolean("Active");
                        detail.volume = res.getInt("Volume");
                    }
                    return detail;
                } else {
                    throw new AppNotFoundException();
                }
            }
        }
    }

    private static ArrayList<AppDetailVO.AppPlan> getAppPlans(Connection con, String appid, Account user) throws SQLException{
        ArrayList<AppDetailVO.AppPlan> plans = new ArrayList<>();
        try (PreparedStatement stat = con.prepareStatement("select *,(case when exists (select * from Possesses where appid=AppPlans.appid and planid=AppPlans.planid and uid=?) then 1 else 0 end) Bought from AppPlans where AppID = ?")) {
            stat.setNString(1, user.getEmail());
            stat.setNString(2, appid);
            try (ResultSet res = stat.executeQuery()) {
                while(res.next()) {
                    AppDetailVO.AppPlan plan = new AppDetailVO.AppPlan();
                    plan.id = res.getString("PlanID");
                    plan.name = res.getNString("Name");
                    plan.duration = res.getInt("Duration");
                    plan.explanation = res.getNString("Explanation");
                    plan.price = res.getDouble("Price");
                    plan.oriprice = res.getDouble("OriginalPrice");
                    plan.isMain = res.getBoolean("IsMainPlan");
                    plan.bought = res.getBoolean("Bought");
                    plans.add(plan);
                }
                return plans;
            }
        }
    }
}
