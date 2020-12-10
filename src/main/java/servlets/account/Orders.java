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
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        int page = Utils.tryParseInt(req.getParameter("page"), 1),
                pageSize = Utils.tryParseInt(req.getParameter("size"), 10);


        if (user == null) {
            res.put("success", false);
            res.put("reason", "Please login first");
        } else if (!"user".equals(user.getRole())) {
            res.put("success", false);
            res.put("reason", "Only users can see orders");
        } else {
            try {
                Pair<Integer, OrderVO[]> orders = OrdersDAO.getOrders(user, page, pageSize);
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
