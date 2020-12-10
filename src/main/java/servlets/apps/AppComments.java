package servlets.apps;

import DAO.AppCommentsDAO;
import VO.AppCommentVO;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/appcomments")
public class AppComments extends HttpServlet {
    /*
    * appid：要查询的app
    * orderby：排序规则：rating-highest、rating-lowest、date-latest、date-oldest，默认rating-highest
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        String appid = req.getParameter("appid");
        String orderby = req.getParameter("orderby");

        if (appid == null) {
            res.put("success", "false");
            res.put("reason", "missing appid");
        } else {
            AppCommentsDAO.Order order;
            if ("rating-lowest".equals(orderby)) {
                order = AppCommentsDAO.Order.RATING_HIGHEST;
            } else if ("date-latest".equals(orderby)) {
                order = AppCommentsDAO.Order.DATE_LATEST;
            } else if ("date-oldest".equals(orderby)) {
                order = AppCommentsDAO.Order.DATE_OLDEST;
            } else {
                order = AppCommentsDAO.Order.RATING_HIGHEST;
            }
            try {
                AppCommentVO[] comments = AppCommentsDAO.getAppComments(appid, order);
                res.put("success", true);
                res.put("count", comments.length); //如果分页了，这里是总个数。现在没分页，所以显得有点多余，但便于以后改进
                res.put("comments", comments);
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
