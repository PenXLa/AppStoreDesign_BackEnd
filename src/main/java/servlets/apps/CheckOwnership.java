package servlets.apps;

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

@WebServlet("/hasbought")
public class CheckOwnership extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        String appid = req.getParameter("appid");
        Account user = AccountUtils.getUser(req.getCookies());

        if (appid == null) {
            res.put("success", false);
            res.put("reason", "Missing Appid");
        } else if (user == null) {
            res.put("success", false);
            res.put("reason", "Please login first");
        } else {
            try {
                res.put("bought", user.hasBoughtApp(appid));
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException e) {
                res.put("success", false);
                res.put("reason", "DB Error");
            }
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
