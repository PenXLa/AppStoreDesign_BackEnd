package servlets.publisherAPI;

import DAO.IconTypeDAO;
import com.alibaba.fastjson.JSONObject;
import kernel.Account;
import kernel.AccountUtils;
import utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet("/publisher/changeappicon")
@MultipartConfig
public class ChangeAppIcon extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();

        String appid = req.getParameter("appid");
        Part filePart = req.getPart("file");
        String ext = Utils.getExtName(Paths.get(filePart.getSubmittedFileName()).getFileName().toString());
        Account user = AccountUtils.getUser(req.getCookies());

        if (user == null || !"seller".equals(user.getRole())) {
            Utils.setJSONError(res, "no_login", "please login as a seller first.");
        } else if (appid == null) {
            Utils.setJSONError(res, "missing_appid", "missing appid");
        } else if (ext == null)  {
            Utils.setJSONError(res, "missing_fileext", "missing file ext");
        } else {
            String newFileName = getServletContext().getRealPath("/images/icon/") + appid + "." + ext;
            try (
                InputStream fileContent = filePart.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(fileContent);
                FileOutputStream fos = new FileOutputStream(newFileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
            ) {
                IconTypeDAO.changeIconType(user, appid, ext);
                while (true) {
                    int b = bis.read();
                    if (b == -1) break;
                    bos.write(b);
                }
                res.put("success", true);
            } catch (SQLException e) {
                Utils.setJSONError(res, "db_error", "DB Error");
                e.printStackTrace();
            } catch (Exception e) {
                Utils.setJSONError(res, "io_error", "IO Error");
                e.printStackTrace();
            }
        }
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
