package servlets.account;

import kernel.AccountUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class Logout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
            for (Cookie ck : cookies)
                if (AccountUtils.COOKIE_USER.equals(ck.getName()) ||
                        AccountUtils.COOKIE_CHECK_CODE.equals(ck.getName())) {
                    ck.setMaxAge(0); ck.setPath("/");
                    resp.addCookie(ck);
                }
    }
}
