package servlets.account;

import DAO.MessagesDAO;
import VO.MessageVO;
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

@WebServlet("/messages")
public class Messages extends HttpServlet {
    private final int DEFAULT_PAGE_SIZE = 15;
    /*
    * 参数
    * action，动作。可以是get（获取消息列表）、setRead（设置已读），setUnread（设置未读），delete（删除）
    * 若是get：
    * page
    * size
    * unreadonly：只看未读，无值
    *
    * 若是setRead、setUnread、delete
    * mid：要操作的消息
    *
    *
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        String action = req.getParameter("action");
        if (user == null) {
            res.put("success", false);
            res.put("reason", "Please login first");
        } else if ("get".equals(action)) {
            int page = Utils.tryParseInt(req.getParameter("page"), 1);
            int pageSize = Utils.tryParseInt(req.getParameter("size"), DEFAULT_PAGE_SIZE);
            boolean unreadOnly = req.getParameter("unreadonly") != null;
            try {
                Pair<Integer, MessageVO[]> messages = MessagesDAO.getMessages(user, unreadOnly, page, pageSize);
                res.put("messages", messages.getRight());
                res.put("total", messages.getLeft());
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
                res.put("success", false);
                res.put("reason", "DB Error");
            }
        } else if ("setRead".equals(action) || "setUnread".equals(action)) {
            String mid = req.getParameter("mid");
            if (mid == null) Utils.setJSONError(res, "missing mid");
            else {
                try {
                    MessagesDAO.setReadStat(user, mid, "setRead".equals(action));
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                    Utils.setJSONError(res, "DB Error");
                }
            }
        } else if ("delete".equals(action)) {
            String mid = req.getParameter("mid");
            if (mid == null) Utils.setJSONError(res, "missing mid");
            else {
                try {
                    MessagesDAO.delete(user, mid);
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                    Utils.setJSONError(res, "DB Error");
                }
            }
        } else {
            Utils.setJSONError(res, "Undefined action");
        }



        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
