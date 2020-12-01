package servlets.publisherAPI;

import DAO.publisher.AppInfoModifier;
import com.alibaba.fastjson.JSONObject;
import kernel.Account;
import kernel.AccountUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/publisher/updateappinfo")
public class ChangeAppInfo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        if ("seller".equals(user.getRole())) { //验证权限
            String action = req.getParameter("action"); //获取动作
            if ("enable".equals(action)) { //上架软件
                try {
                    AppInfoModifier.setEnabled(user.getPublisher(), req.getParameter("appid"), true);
                    json.put("success", true);
                } catch (SQLException | ClassNotFoundException e) {
                   e.printStackTrace();
                    json.put("success", false);
                    json.put("reason", "DB Error");
                } catch (AppInfoModifier.PermissionDeniedException e) {
                    json.put("success", false);
                    json.put("reason", "Permission Denied");
                }
            } else if ("disable".equals(action)) { //下架软件
                try {
                    AppInfoModifier.setEnabled(user.getPublisher(), req.getParameter("appid"), false);
                    json.put("success", true);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    json.put("success", false);
                    json.put("reason", "DB Error");
                } catch (AppInfoModifier.PermissionDeniedException e) {
                    json.put("success", false);
                    json.put("reason", "Permission Denied");
                }
            } else {
                json.put("success", false);
                json.put("reason", "Undefined Action");
            }
        } else {
            json.put("success", false);
            json.put("reason", "Permission Denied");
        }
        resp.getWriter().println(json.toJSONString());
    }
}
