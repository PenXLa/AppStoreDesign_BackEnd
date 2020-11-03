package servlets.account;

import com.alibaba.fastjson.JSONObject;
import kernel.AccountUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class Login extends HttpServlet {
    public static final int MAX_PWD_LEN = 30;
    public static final int LOGIN_SUCCESS = 1,
            LOGIN_WRONG = 3,
            LOGIN_DB_ERROR = 4,
            LOGIN_INVALID_PARAM = 5;
    //获取登录参数，通过引用返回。函数返回值是参数是否合法
    Pair<String, String> getLoginParam(HttpServletRequest req) {
        String email, pwd;
        email = req.getParameter("email");
        pwd = req.getParameter("pwd");
        if (email==null || pwd == null ||
            pwd.length() > MAX_PWD_LEN || pwd.length()==0 || email.length()==0) return null;
        email = email.trim().toLowerCase();
        return new ImmutablePair<>(email, pwd);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json;charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8"); //post中文参数防乱码
        JSONObject json = new JSONObject();
        int res = 0;
        var par = getLoginParam(req);
        if (par!=null) {
            String email = par.getLeft(), pwd = par.getRight();
            try {
                String rem = req.getParameter("remember");
                res = AccountUtils.login(resp, email, pwd, "on".equals(rem))? LOGIN_SUCCESS : LOGIN_WRONG;
            } catch (SQLException | ClassNotFoundException e) {
                res = LOGIN_DB_ERROR;
                e.printStackTrace();
            }
        } else res = LOGIN_INVALID_PARAM;

        json.put("result", res);
        resp.getWriter().write(json.toJSONString());
    }
}
