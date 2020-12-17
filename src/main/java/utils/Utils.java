package utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Utils {
    public static Connection connectDB(String database) throws ClassNotFoundException, SQLException {
        final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                url = "jdbc:sqlserver://impxl.cn:1433;databasename=",
                pwd = "tyRuZf18",
                user = "AppStoreWeb";
        Class.forName(driver);
        Connection con = DriverManager.getConnection(url+database, user, pwd);
        return con;
    }



    //尝试转换string到int。如果失败，返回def
    public static int tryParseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
    public static Integer tryParseInteger(String s, Integer def) {
        try {
            return Integer.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }
    public static Double tryParseDouble(String s, Double def) {
        try {
            return Double.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }
    public static Long tryParseLong(String s, Long def) {
        try {
            return Long.valueOf(s);
        } catch (Exception e) {
            return def;
        }
    }


    public static String getAppIconURL(String appid, String iconType) {
        return "/images/icon/" + appid + "." + iconType;
    }

    public static void setJSONError(JSONObject res, String reason) {
        res.put("success", false);
        res.put("reason", reason);
    }
    public static void setJSONError(JSONObject res, String code, String reason) {
        res.put("success", false);
        res.put("reason", reason);
        res.put("errcode", code);
    }

    public static String getExtName(String file) {
        if (file == null) return null;
        // split用的是正则，所以需要用 //. 来做分隔符
        String[] split = file.split("\\.");
        //注意判断截取后的数组长度，数组最后一个元素是后缀名
        if (split.length > 1) {
            return split[split.length - 1];
        } else {
            return "";
        }
    }
}
