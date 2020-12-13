package kernel;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import utils.ErrorCodes;
import utils.SelectQuery;
import utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.function.Predicate;


public class AccountUtils {
    public static final int HASH_COST = 10;
    public static final int COOKIE_LOGIN_AGE = 5 *24*60*60;//5天
    public static final String COOKIE_USER = "uid",
                                COOKIE_CHECK_CODE = "token", //校验码，aes(json(CHECK_CODE_USER:hash(用户名), CHECK_CODE_EXPIRE:过期时间))
                                CHECK_CODE_USER = "uid", CHECK_CODE_EXPIRE = "expire";






    public static @NotNull String calcHash(@NotNull String pwd) {
        return BCrypt.withDefaults().hashToString(HASH_COST, pwd.toCharArray());
    }
    //计算校验码
    private static String calcCheckCode(@NotNull String uid) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        JSONObject json = new JSONObject();
        json.put(CHECK_CODE_USER, calcHash(uid));
        json.put(CHECK_CODE_EXPIRE, System.currentTimeMillis() + COOKIE_LOGIN_AGE*1000);
        return AES.encrypt(json.toJSONString());
    }
    private static boolean verifyCheckCode(@NotNull String uid, String checkCode) {
        try {
            JSONObject json = JSON.parseObject(AES.decrypt(checkCode));
            return BCrypt.verifyer().verify(uid.toCharArray(), json.getString(CHECK_CODE_USER)).verified &&
                    System.currentTimeMillis() < json.getLongValue(CHECK_CODE_EXPIRE);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean login(HttpServletResponse resp, String email, String pwd, boolean remember) throws SQLException, ClassNotFoundException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        try(
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = new SelectQuery().select("UID, Email, PassHash").from("Users").where("Email=?", email).toStatement(con);
            ResultSet res = stat.executeQuery();
        ) {
            if (res.next() && BCrypt.verifyer().verify(pwd.toCharArray(), res.getString("PassHash")).verified) {
                String uid = res.getNString("UID");
                Cookie uidCK = new Cookie(COOKIE_USER, uid);
                Cookie chkCodeCK = new Cookie(COOKIE_CHECK_CODE, calcCheckCode(uid));
                if (remember) {
                    uidCK.setMaxAge(COOKIE_LOGIN_AGE);
                    chkCodeCK.setMaxAge(COOKIE_LOGIN_AGE);
                }
                uidCK.setPath("/"); chkCodeCK.setPath("/");
                resp.addCookie(uidCK);
                resp.addCookie(chkCodeCK);

                return true;
            } else return false;
        }
    }

    //这是对cookie进行aes加密，不查询数据库的方案
    private static boolean loginByCookie(Cookie[] cookies) throws SQLException, ClassNotFoundException {
        if (cookies == null) return false;
        String user = null, checkCode = null;
        for (Cookie ck : cookies) {
            if (COOKIE_USER.equals(ck.getName())) user = ck.getValue();
            else if (COOKIE_CHECK_CODE.equals(ck.getName())) checkCode = ck.getValue();
        }
        return user!=null && checkCode!=null && verifyCheckCode(user, checkCode);
    }

    //从cookies获取用户。获取失败返回null
    public static Account getUser(Cookie[] cookies) {
        try {
            if (loginByCookie(cookies)) {
                for (Cookie ck : cookies)
                    if (COOKIE_USER.equals(ck.getName())) return new Account(ck.getValue());
            }
        } catch (ClassNotFoundException | SQLException | Account.InvalidUIDException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public static final int REGISTER_SUCCESS = 0,
            REGISTER_FAIL_NAME_EXISTS = 1,
            REGISTER_FAIL_INVALID_NAME = 2,
            REGISTER_FAIL_INVALID_PWD = 3;
    public static int register(String email, String name, String pwd) throws SQLException, ClassNotFoundException {
        if (email == null || email.trim().equals("")) return REGISTER_FAIL_INVALID_NAME;
        if (pwd == null || pwd.equals("")) return REGISTER_FAIL_INVALID_PWD;
        email = email.trim();

        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            CallableStatement stat = con.prepareCall("{ call UserRegister(?, ?, ?) }")
        ) {
            stat.setNString("email", email);
            stat.setNString("passhash", calcHash(pwd));
            stat.setNString("name", name);
            stat.execute();
            return REGISTER_SUCCESS;
        } catch (SQLException e) {
            if (e.getErrorCode()== ErrorCodes.REGISTER_EMAIL_USED) return REGISTER_FAIL_NAME_EXISTS;
            else throw e;
        }
    }

    public static final int DA_FAIL_NAME_NOT_EXISTS = 1,
                            DA_INVALID_NAME = 2,
                            DA_SUCCESS = 0;
  //  public static int destroyAccount(String email) throws SQLException, ClassNotFoundException {
//        int code = 0;
//
//        if (email == null || email.trim().equals("")) return DA_INVALID_NAME;
//        email = email.trim();
//
//        Connection con = Utils.connectDB("AppStoreDesign");
//        PreparedStatement stat = con.prepareStatement("select * from Users where Email=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
//        stat.setNString(1, email);
//        var res = stat.executeQuery();
//        if (!res.next()) code = DA_FAIL_NAME_NOT_EXISTS;
//        else {
//            res.deleteRow();
//            code = DA_SUCCESS;
//        }
//        res.close();
//        stat.close();
//        con.close();
//        return code;
   // }

    public static final int CP_INVALID_NAME = 1,
                            CP_NAME_NOT_EXISTS = 2,
                            CP_INVALID_PWD = 3,
                            CP_SUCCESS = 0;



    //用于确认用户是否有访问权限的函数
    //如果未登录，自动跳转到登录页面，函数返回UNKNOWN
    //如果登录了并且有权限，函数返回HAS
    //没有权限则返回NO
    public enum UserPermissionResult { HAS, NO, UNKNOWN }
    public static UserPermissionResult checkUser(HttpServletRequest req, HttpServletResponse resp, Predicate<Account> hasPermission) throws IOException {
        Account user = getUser(req.getCookies());
        if (user == null) {
            resp.sendRedirect("/login.html?url=" + req.getRequestURL()); //没有登录，先要求登录
            return UserPermissionResult.UNKNOWN;
        } else if (!hasPermission.test(user)) {
            resp.sendRedirect("/"); //登录用户不对，重定向到首页
            return UserPermissionResult.NO;
        }
        return UserPermissionResult.HAS;
    }

}
