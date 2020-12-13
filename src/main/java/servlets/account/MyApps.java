package servlets.account;

import DAO.MyAppsDAO;
import VO.UserAppVO;
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

@WebServlet("/myapps")
public class MyApps extends HttpServlet {
    public static final int DEFAULT_PAGE_SIZE = 15;
    /*
    * 参数：
    * page
    * size
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        int page = Utils.tryParseInt(req.getParameter("page"), 1), pageSize = Utils.tryParseInt(req.getParameter("size"), DEFAULT_PAGE_SIZE);
        if (user == null) {
            res.put("success", false);
            res.put("reason", "Please login first");
        } else {
            try {
                Pair<Integer, UserAppVO[]> apps = MyAppsDAO.userApps(user, page, pageSize);
                res.put("count", apps.getLeft());
                res.put("apps", apps.getRight());
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                res.put("success", false);
                res.put("reason", "DB Error");
            }
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
