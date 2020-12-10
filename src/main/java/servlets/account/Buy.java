package servlets.account;

import DAO.BuyDAO;
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

@WebServlet("/buy")
public class Buy extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        String appid = req.getParameter("appid");
        String planid = req.getParameter("planid");
        if (user == null) {
            res.put("success", false);
            res.put("reason", "Please login first");
        } else if (appid == null) {
            res.put("success", false);
            res.put("reason", "Missing appid");
        } else if (planid == null) {
            res.put("success", false);
            res.put("reason", "Missing planid");
        } else {
            try {
                res.put("orderid", BuyDAO.buy(user, appid, planid));
                res.put("success", true);
            } catch (BuyDAO.InsufficientBalanceException e) {
                res.put("success", false);
                res.put("reason", "Insufficient balance");
                res.put("errorcode", "no_money");
            } catch (BuyDAO.AlreadyOwnException e) {
                res.put("success", false);
                res.put("reason", "Already own");
                res.put("errorcode", "rebuy");
            } catch (BuyDAO.InvalidProductException e) {
                res.put("success", false);
                res.put("reason", "Invalid product");
                res.put("errorcode", "invalid_product");
            } catch (SQLException | ClassNotFoundException e) {
                res.put("success", false);
                res.put("reason", "DB Error");
                res.put("errorcode", "db_error");
                e.printStackTrace();
            }
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
