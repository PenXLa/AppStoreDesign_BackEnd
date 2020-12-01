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
        JSONObject res = new JSONObject();
        if (user != null) {
            res.put("loggedIn", true);
            res.put("name", user.getName());
            res.put("email", user.getEmail());
            res.put("role", user.getRole());
        } else {
            res.put("loggedIn", false);
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
