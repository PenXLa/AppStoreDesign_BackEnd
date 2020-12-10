package servlets.account;

import DAO.PayDAO;
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

@WebServlet("/pay")
public class Pay extends HttpServlet {
    private final long MIN_PAY_AMOUNT = 0, MAX_PAY_AMOUNT = 99999999999L;
    /*
    * 参数：amount
    * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        String payAmount_Str = req.getParameter("amount");
        Account user = AccountUtils.getUser(req.getCookies());
        if (user == null) {
            res.put("success", false);
            res.put("reason", "Please login first");
        } else if (!"user".equals(user.getRole())) {
            res.put("success", false);
            res.put("reason", "Admins and publishers are not allowed to pay");
        } else if (payAmount_Str == null) {
            res.put("success", false);
            res.put("reason", "No pay amount");
        } else {
            try {
                long payAmount = (long)(Double.parseDouble(payAmount_Str)*1000);
                if (payAmount < MIN_PAY_AMOUNT || payAmount > MAX_PAY_AMOUNT) {
                    res.put("success", false);
                    res.put("reason", "Pay amount not in valid range");
                } else {
                    PayDAO.pay(user, payAmount);
                    res.put("success", true);
                }
            } catch (NumberFormatException e) {
                res.put("success", false);
                res.put("reason", "Pay amount format error");
            } catch (SQLException | ClassNotFoundException e) {
                res.put("success", false);
                res.put("reason", "DB Error");
                e.printStackTrace();
            }
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
