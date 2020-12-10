package servlets.account;

import com.alibaba.fastjson.JSONObject;
import kernel.Account;
import kernel.AccountUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/userinfo")
public class AccountStatus extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Account user = AccountUtils.getUser(req.getCookies());
        JSONObject res = null;
        if (user != null) {
            res = (JSONObject)JSONObject.toJSON(user);
            res.put("loggedIn", true);
        } else {
            res.put("loggedIn", false);
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
