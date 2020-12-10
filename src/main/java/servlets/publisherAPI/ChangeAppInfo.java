package servlets.publisherAPI;

import DAO.publisher.AppInfoModifier;
import VO.AppDetailVO;
import com.alibaba.fastjson.JSONObject;
import kernel.Account;
import kernel.AccountUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;

@WebServlet("/publisher/updateappinfo")
public class ChangeAppInfo extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        String appid = req.getParameter("appid");
        try {
            if (appid == null) {
                json.put("success", false);
                json.put("reason", "Missing Appid");
            } else if (user == null || !user.hasSellerPermission(appid)) {
                json.put("success", false);
                json.put("reason", "Permission Denied");
            } else {
                String action = req.getParameter("action"); //获取动作
                if ("enable".equals(action)) { //上架软件
                    AppInfoModifier.setEnabled(appid, true);
                    json.put("success", true);
                } else if ("disable".equals(action)) { //下架软件
                    AppInfoModifier.setEnabled(req.getParameter("appid"), false);
                    json.put("success", true);
                } else if ("setbasicinfo".equals(action)) {
                    AppInfoModifier.setBasicInfo(appid, JSONObject.parseObject(URLDecoder.decode(req.getParameter("data"),"UTF-8"), AppDetailVO.class));
                    json.put("success", true);
                } else {
                    json.put("success", false);
                    json.put("reason", "Undefined Action " + action);
                }
            }
            resp.getWriter().println(json.toJSONString());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            json.put("success", false);
            json.put("reason", "DB Error");
        }
    }
}
