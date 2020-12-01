package servlets.publisherAPI;

import VO.AppSearchResult;
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
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/publisher/apps")
public class PublishedApps extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(searchApp(req).toJSONString());
    }

    private JSONObject searchApp (HttpServletRequest req) {
        JSONObject res = new JSONObject();
        try {
            Account user = AccountUtils.getUser(req.getCookies());
            if (user != null && "seller".equals(user.getRole())) {
                AppSearchResult searchResult = DAO.AppSearcher.publisherSearch(user.getPublisher());
                res.put("searchResult", searchResult);
                res.put("success", true);
            } else {
                res.put("reason", "Permission Denied");
                res.put("success", false);
            }
        } catch (SQLException | ClassNotFoundException e) {
            res.put("success", false);
            res.put("resaon", "DB Error");
            e.printStackTrace();
        }
        return res;

    }
}
