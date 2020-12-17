package DAO;

import VO.AppDetailVO;
import kernel.Account;
import utils.UpdateQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePlanInfoDAO {
    //支持修改code、name、explanation、price、oriprice、duration、active
    public static void changePlanInfo(Account user, AppDetailVO.AppPlan plan) throws SQLException, ClassNotFoundException {
        UpdateQuery sql = new UpdateQuery();
        sql.update("AppPlans").where("PlanID=?", plan.id) //选中plan
            .where("PlanID IN (select PlanID from AppPlans AP INNER JOIN Applications A on AP.AppID=A.AppID WHERE Publisher=?)", user.getPublisher()); //权限限制
        if (plan.code != null)
            sql.set("PlanCode=?", plan.code);
        if (plan.name != null)
            sql.set("Name=?", plan.name);
        if (plan.explanation != null)
            sql.set("Explanation=?", plan.explanation);
        if (plan.price != null)
            sql.set("Price=?", plan.price);
        if (plan.oriprice != null)
            sql.set("OriginalPrice=?", plan.oriprice);
        if (plan.duration != null)
            sql.set("Duration=?", plan.duration*60*60*24);
        if (plan.active != null)
            sql.set("Active=?", plan.active);

        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = sql.toStatement(con);
        ) {
            stat.executeUpdate();
        }
    }
}
