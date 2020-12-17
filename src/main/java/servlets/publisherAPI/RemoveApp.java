package servlets.publisherAPI;

import DAO.AppCreaterDAO;
import DAO.AppRemoverDAO;
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

@WebServlet("/publisher/removeapp")
public class RemoveApp extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        String appid = req.getParameter("appid");
        if (appid == null) {
            Utils.setJSONError(res, "missparam","missing appid");
        } else if (user == null || !"seller".equals(user.getRole())) {
            Utils.setJSONError(res, "notlogin","Please login as seller first");
        } else {
            try {
                AppRemoverDAO.removeApp(user, appid);
                res.put("appid", appid);
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
                Utils.setJSONError(res, "db_error","DB Error");
            } catch (AppRemoverDAO.PermissionDeniedException e) {
                Utils.setJSONError(res, "wrong_user", "Permission Denied");
            } catch (AppRemoverDAO.StillHasUsersException e) {
                Utils.setJSONError(res, "still_using", "Still has user");
            }
        }


        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
