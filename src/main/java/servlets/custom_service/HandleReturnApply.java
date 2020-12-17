package servlets.custom_service;

import DAO.custom_service.ReturnsDAO;
import com.alibaba.fastjson.JSON;
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

@WebServlet("/customservice/handlereturn")
public class HandleReturnApply extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        String action = req.getParameter("action");
        String rid = req.getParameter("rid");
        String reason = req.getParameter("reason");
        Account user = AccountUtils.getUser(req.getCookies());
        if (user == null || !"cs".equals(user.getRole())) {
            Utils.setJSONError(res, "please login as a custom service first");
        } else if (action == null || rid == null) {
            Utils.setJSONError(res, "missing parameter");
        } else {
            try {
                boolean accept = "accept".equals(action);
                ReturnsDAO.handleApply(rid, accept, accept?null:reason); //只有accept=false才使用reason
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
