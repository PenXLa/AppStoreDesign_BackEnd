package servlets.apps;

import DAO.DoCommentDAO;
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

@WebServlet("/newcomment")
public class DoComment extends HttpServlet {
    private final double MIN_RATING = 0, MAX_RATING = 10;

    /*
    * 参数：
    * appid：要评论的app
    * content：评论内容
    * rating：评分
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();

        String appid = req.getParameter("appid");
        String content = req.getParameter("content");
        String rating_str = req.getParameter("rating");
        try {
            double rating = Double.parseDouble(rating_str);
            if (appid == null) {
                res.put("success", false);
                res.put("reason", "Missing appid");
            } else if (rating_str == null) {
                res.put("success", false);
                res.put("reason", "Missing rating");
            } else {
                Account user = AccountUtils.getUser(req.getCookies());
                if (user == null || !user.hasBoughtApp(appid)) {
                    res.put("success", false);
                    res.put("reason", "Permission Denied");
                } else {
                    if (rating < MIN_RATING) rating = MIN_RATING;
                    else if (rating > MAX_RATING) rating = MAX_RATING;
                    DoCommentDAO.comment(user, appid, content, rating);
                    res.put("success", true);
                }
            }
        } catch (SQLException|ClassNotFoundException e) {
            res.put("success", false);
            res.put("reason", "DB Error");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            res.put("success", false);
            res.put("reason", "Wrong Rating Format" + rating_str);
        }

        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
