package servlets.publisherAPI;

import DAO.ChangePlanInfoDAO;
import VO.AppDetailVO;
import com.alibaba.fastjson.JSONObject;
import jdk.jshell.execution.Util;
import kernel.Account;
import kernel.AccountUtils;
import utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;

@WebServlet("/publisher/changeplaninfo")
public class ChangePlanInfo extends HttpServlet {
    /*
    * 参数：
    * data：包含参数的json，内容如下：
    *
    * planid：待修改的plan的id
    * code：可选
    * name：可选
    * explanation：可选
    * price：可选
    * oriprice：可选
    * duration：可选
    * active：可选
    *
    * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        String data = req.getParameter("data");
        if (user == null || !"seller".equals(user.getRole())) {
            Utils.setJSONError(res, "Please login as seller first");
        } else if (data == null) {
            Utils.setJSONError(res, "missing data");
        } else {
            AppDetailVO.AppPlan plan = JSONObject.parseObject(URLDecoder.decode(data, "UTF-8"), AppDetailVO.AppPlan.class);
            try {
                ChangePlanInfoDAO.changePlanInfo(user, plan);
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
                Utils.setJSONError(res, "DB Error");
            }

        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
