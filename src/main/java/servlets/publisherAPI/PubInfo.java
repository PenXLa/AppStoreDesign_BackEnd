package servlets.publisherAPI;

import DAO.publisher.PubInfoDAO;
import VO.publisher.PubInfoVO;
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

@WebServlet("/publisher/info")
public class PubInfo extends HttpServlet {

    /*
    * 参数：action: [read/update]读取信息还是更新信息，默认读取
    * name: 如果要更新信息，新的名字
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");

        JSONObject json = new JSONObject();
        Account user = AccountUtils.getUser(req.getCookies());
        if ("seller".equals(user.getRole())) { //验证权限
            String action = req.getParameter("action");
            if (action == null || "read".equals(action)) { //===读取信息
                try {
                    PubInfoVO info = PubInfoDAO.getPubInfo(user);
                    json.put("success", true);
                    json.put("info", info);
                } catch (SQLException | ClassNotFoundException e) {
                    json.put("success", false);
                    json.put("reason", "DB Error");
                    e.printStackTrace();
                } catch (PubInfoDAO.PublisherNotFoundException e) {
                    json.put("success", false);
                    json.put("reason", "No such publisher");
                }
            } else { //==========================================更新信息
                PubInfoVO info = new PubInfoVO();
                info.name = req.getParameter("name");
                try {
                    PubInfoDAO.setPubInfo(user, info);
                    json.put("success", true);
                } catch (SQLException | ClassNotFoundException e) {
                    json.put("success", false);
                    json.put("reason", "DB Error");
                    e.printStackTrace();
                } catch (PubInfoDAO.PublisherNotFoundException e) {
                    json.put("success", false);
                    json.put("reason", "No such publisher");
                }
            }
        } else {
            json.put("success", false);
            json.put("reason", "Permission denied");
        }
        resp.getWriter().println(json.toJSONString());
    }
}
