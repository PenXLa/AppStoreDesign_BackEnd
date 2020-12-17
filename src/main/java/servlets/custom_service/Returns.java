package servlets.custom_service;

import DAO.custom_service.ReturnsDAO;
import VO.ReturnVO;
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

@WebServlet("/customservice/returns")
public class Returns extends HttpServlet {
    private static final int DEFAULT_PAGE_SIZE = 20;
    /*
    * page
    * pageSize
    *
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        int page = Utils.tryParseInt(req.getParameter("page"), 1);
        int pageSize = Utils.tryParseInt(req.getParameter("size"), DEFAULT_PAGE_SIZE);

        if (user == null) {
            Utils.setJSONError(res, "please login as custom service first");
        } else {
            try {
                Pair<Integer, ReturnVO[]> applies = ReturnsDAO.getReturns(user, page, pageSize);
                res.put("applies", applies.getRight());
                res.put("total", applies.getLeft());
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
                Utils.setJSONError(res, "DB Error");
            }
        }


        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
