package DAO;

import VO.AppDetailVO;
import VO.publisher.PublisherAppDetailVO;
import kernel.Utils;
import servlets.publisherAPI.PublisherAppDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppDetailDAO {
    public static class AppNotFoundException extends Exception { }
    public static AppDetailVO getAppDetail(String appid) throws SQLException, ClassNotFoundException, AppNotFoundException {
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
                    if (res.wasNull()) detail.hardDisk = -1;
                    detail.RAM = res.getLong("Req_RAM");
                    if (res.wasNull()) detail.RAM = -1;
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

                    var plans = getAppPlans(con, appid);
                    detail.subscribePlans = new AppDetailVO.AppPlan[plans.size()-1]; //假设了有且只有一个MainPlan
                    int inx = 0;
                    for (var plan : plans)
                        if (plan.isMain) detail.mainPlan = plan;
                        else detail.subscribePlans[inx++] = plan;

                    return detail;
                } else {
                    throw new AppNotFoundException();
                }
            }
        }
    }

    private static ArrayList<AppDetailVO.AppPlan> getAppPlans(Connection con, String appid) throws SQLException{
        ArrayList<AppDetailVO.AppPlan> plans = new ArrayList<>();
        try (PreparedStatement stat = con.prepareStatement("select * from AppPlans where AppID = ?")) {
            stat.setNString(1, appid);
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
                    plans.add(plan);
                }
                return plans;
            }
        }
    }


    public static PublisherAppDetailVO getPublisherAppDetail(String publisher, String appid) throws SQLException, ClassNotFoundException, AppNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = con.prepareStatement("select * from AppDetails where AppID = ? and Publisher = ?")
        ) {
            stat.setNString(1, appid);
            stat.setNString(2, publisher);
            try (ResultSet res = stat.executeQuery()) {
                if (res.next()) {
                    PublisherAppDetailVO detail = new PublisherAppDetailVO();
                    detail.active = res.getBoolean("Active");
                    return detail;
                } else {
                    throw new AppNotFoundException();
                }
            }
        }
    }
}
