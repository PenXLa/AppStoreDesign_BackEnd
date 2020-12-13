package servlets.account;

import DAO.ApplyReturnDAO;
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

@WebServlet("/applyreturn")
public class ApplyReturn extends HttpServlet {
    /*
    * 退order或app
    * 有oid参数的话，退order
    * 没有oid参数，但有appid参数，就退app
    * 都没有，报错
    *
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        String appid = req.getParameter("appid");
        String oid = req.getParameter("oid");
        String reason = req.getParameter("reason");
        Account user = AccountUtils.getUser(req.getCookies());
        if (user == null || (appid == null && oid == null)) {
            res.put("success", false);
            res.put("reason", "missing appid and oid or not logged in");
            res.put("errorcode", "miss_param");
        } else {
            try {
                if (oid != null)
                    ApplyReturnDAO.returnOrder(user, oid, reason);
                else
                    ApplyReturnDAO.returnApp(user, appid, reason);
                res.put("success", true);
            } catch (ApplyReturnDAO.AlreadyAppliedException e) {
                res.put("success", false);
                res.put("reason", "Already applied");
                res.put("errorcode", "applied");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                res.put("success", false);
                res.put("reason", "DB Error");
                res.put("errorcode", "db_error");
            } catch (ApplyReturnDAO.WrongOrderIDException e) {
                e.printStackTrace();
                res.put("success", false);
                res.put("reason", "Wrong order");
                res.put("errorcode", "wrong_order");
            }
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
