package servlets.custom_service;

import DAO.AppSearcher;
import DAO.custom_service.BackupDAO;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/restore")
public class Restore extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        try {
            BackupDAO.restore();
            res.put("success", true);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
            res.put("success", false);
        }
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
