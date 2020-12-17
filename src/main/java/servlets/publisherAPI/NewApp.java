package servlets.publisherAPI;

import DAO.AppCreaterDAO;
import com.alibaba.fastjson.JSONObject;
import kernel.Account;
import kernel.AccountUtils;
import utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/publisher/newapp")
public class NewApp extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        if (user == null || !"seller".equals(user.getRole())) {
            Utils.setJSONError(res, "Please login as seller first");
        } else {
            try {
                String appid = AppCreaterDAO.createApp(user);
                res.put("appid", appid);
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
