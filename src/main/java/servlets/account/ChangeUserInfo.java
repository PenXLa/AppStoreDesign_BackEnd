package servlets.account;

import DAO.ChangeUserInfoDAO;
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

@WebServlet("/changeuserinfo")
public class ChangeUserInfo extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        String action = req.getParameter("action");
        Account user = AccountUtils.getUser(req.getCookies());
        try {
            if ("changepasswd".equals(action)) {
                String newPwd = req.getParameter("value");
                String validatingCode = req.getParameter("validatingCode");
                String email = req.getParameter("email");
                if (newPwd == null || validatingCode == null) {
                    res.put("success", false);
                    res.put("reason", "Missing value or validating code");
                } else if (!"1234".equals(validatingCode)) { //固定的邮箱验证码
                    res.put("success", false);
                    res.put("reason", "验证码错误");
                } else {
                    ChangeUserInfoDAO.changePasswd(user == null ? email : user.getEmail(), newPwd);
                    res.put("success", true);
                }
            } else if (user != null) {
                if ("changeusername".equals(action)) {
                    String newName = req.getParameter("value");
                    if (newName == null || newName.trim().equals("")) {
                        res.put("success", false);
                        res.put("reason", "Missing value");
                    } else {
                        ChangeUserInfoDAO.changeUserName(user, newName.trim());
                        res.put("success", true);
                    }
                } else {
                    res.put("success", false);
                    res.put("reason", "Undefined action " + action);
                }
            } else {
                res.put("success", false);
                res.put("reason", "Please login first");
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
            res.put("success", false);
            res.put("reason", "DB Error");
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
