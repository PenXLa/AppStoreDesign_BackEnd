package servlets.account;

import DAO.RegisterDAO;
import com.alibaba.fastjson.JSONObject;
import kernel.AccountUtils;
import utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class Register extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();
        String name = req.getParameter("name");
        String passwd = req.getParameter("passwd");
        String email = req.getParameter("email");
        String invitation = req.getParameter("invitation");
        String pubName = req.getParameter("pubName");

        if (name == null || passwd == null || email == null) {
            Utils.setJSONError(res, "missing parameter");
        } else {
            try {
                if (invitation != null)
                    RegisterDAO.register(email, name, AccountUtils.calcHash(passwd), invitation);
                else if (pubName != null)
                    RegisterDAO.registerPublisher(email, name, AccountUtils.calcHash(passwd), pubName);
                else
                RegisterDAO.register(email, name, AccountUtils.calcHash(passwd));
                res.put("success", true);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
                Utils.setJSONError(res, "DB Error");
            } catch (RegisterDAO.EmailUsedException e) {
                e.printStackTrace();
                Utils.setJSONError(res, "email_used","email used");
            } catch (RegisterDAO.WrongInvitationException e) {
                e.printStackTrace();
                Utils.setJSONError(res, "wrong_invitation","wrong invitation");
            }
        }


        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
