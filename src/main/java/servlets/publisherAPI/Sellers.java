package servlets.publisherAPI;

import DAO.publisher.SellerDAO;
import VO.SellerVO;
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

@WebServlet("/publisher/sellers")
public class Sellers extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        if (user == null || !user.getIsManager()) {
            Utils.setJSONError(res, "notmanager", "您不是公司管理员");
        } else {
            try {
                SellerVO[] sellers = SellerDAO.getSellers(user.getPublisher());
                res.put("sellers", sellers);
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
                Utils.setJSONError(res, "db_error", "DB Error");
            }
        }
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());

    }
}
