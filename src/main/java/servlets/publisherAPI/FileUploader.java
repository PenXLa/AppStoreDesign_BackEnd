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

@WebServlet("/publisher/appupload")
@MultipartConfig
public class FileUploader extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject res = new JSONObject();

        String appid = req.getParameter("appid");
        Part filePart = req.getPart("file");
        Account user = AccountUtils.getUser(req.getCookies());

        if (user == null || !"seller".equals(user.getRole())) {
            Utils.setJSONError(res, "no_login", "please login as a seller first.");
        } else if (appid == null) {
            Utils.setJSONError(res, "missing_appid", "missing appid");
        } else {
            String newFileName = getServletContext().getRealPath("/files/") + appid + ".exe";
            try (
                    InputStream fileContent = filePart.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(fileContent);
                    FileOutputStream fos = new FileOutputStream(newFileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
            ) {
                while (true) {
                    int b = bis.read();
                    if (b == -1) break;
                    bos.write(b);
                }
                res.put("success", true);
            }
        }
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(res.toJSONString());
    }
}
