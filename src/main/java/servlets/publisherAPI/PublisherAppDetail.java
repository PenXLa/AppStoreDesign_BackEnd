package servlets.publisherAPI;

import DAO.AppDetailDAO;
import VO.AppDetailVO;
import com.alibaba.fastjson.JSONArray;
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

@WebServlet("/publisher/appdetail")
public class PublisherAppDetail extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(getAppDetail(req).toJSONString());
    }

    private JSONObject getAppDetail(HttpServletRequest req) {
        JSONObject json = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        if (!"seller".equals(user.getRole())) {
            json.put("success", false);
            json.put("reason", "Permission Denied");
            return json;
        }

        String appid = req.getParameter("id");
        if (appid == null) {
            json.put("success", false);
            json.put("reason", "missing id");
            return json;
        } else {
            try {
                json.put("detail", AppDetailDAO.getPublisherAppDetail(user.getPublisher(), appid));
                json.put("success", true);
                return json;
            } catch (SQLException | ClassNotFoundException e) {
                json.put("success", false);
                json.put("reason", "DB Error");
                e.printStackTrace();
                return json;
            } catch (AppDetailDAO.AppNotFoundException e) {
                json.put("success", false);
                json.put("reason", "no such app");
                return json;
            }
        }

    }
}
