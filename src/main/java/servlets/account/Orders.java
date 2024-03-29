package servlets.account;

import DAO.OrdersDAO;
import VO.OrderVO;
import com.alibaba.fastjson.JSONObject;
import kernel.Account;
import kernel.AccountUtils;
import org.apache.commons.lang3.tuple.Pair;
import utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/orders")
public class Orders extends HttpServlet {
    /*
    * 参数：
    * page
    * size
    *
    * appid（可选），搜索与appid相关的订单
    * oid（可选），搜索oid这个订单
    * appid和oid不同时用，同时出现时oid优先
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        int page = Utils.tryParseInt(req.getParameter("page"), 1),
                pageSize = Utils.tryParseInt(req.getParameter("size"), 10);
        String appid = req.getParameter("appid");
        String oid = req.getParameter("oid");

        if (user == null) {
            res.put("success", false);
            res.put("reason", "Please login first");
        } else if (!"user".equals(user.getRole())) {
            res.put("success", false);
            res.put("reason", "Only users can see orders");
        } else {
            try {
                Pair<Integer, OrderVO[]> orders = null;
                if (oid != null)
                    orders = OrdersDAO.getOrder(user, oid);
                else if (appid != null)
                    orders = OrdersDAO.getAppOrders(user, appid, page, pageSize);
                else
                    orders = OrdersDAO.getAllOrders(user, page, pageSize);

                res.put("orders", orders.getRight());
                res.put("total", orders.getLeft());
                res.put("success", true);
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
