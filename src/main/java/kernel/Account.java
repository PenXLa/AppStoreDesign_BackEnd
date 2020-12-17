package kernel;

import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    public static class InvalidUIDException extends Exception{}

    private String uid;
    private String email;
    private String username = null;
    private String role = null;
    private String publisher = null;//如果role为seller，此变量存储其所在公司
    private long balance = 0; //余额，单位为0.1分



    private boolean isManager = false; //是否是一个公司的manager

    public String getUid() { return uid; } //因为fastjson会把UID序列化成uID，很难看，所以我就把这个方法名写成了Uid
    public String getEmail() { return email; }
    public String getName() { return username; }
    public String getRole() { return role; }
    public String getPublisher() { return publisher; }
    public long getBalance() { return balance; }
    public boolean getIsManager() { return isManager; } //为了fastjson识别，取了个奇怪名字

    public Account(String uid) throws InvalidUIDException {
        this.uid = uid;
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = new SelectQuery().select("Email, Name, URole").from("Users").where("UID=?", uid).toStatement(con);
                ResultSet res = stat.executeQuery()
        ) {
            if (res.next()) {
                this.email = res.getNString("Email");
                this.username = res.getNString("Name");
                this.role = res.getString("URole");
                if ("seller".equals(this.role)) {
                    try (
                        PreparedStatement detailStat = new SelectQuery().select("S.*, Manager").from("Sellers S INNER JOIN Publishers P ON S.Publisher=P.PubID").where("UID=?", uid).toStatement(con);
                        ResultSet detail = detailStat.executeQuery()
                    ) {
                        if (detail.next()) {
                            this.publisher = detail.getNString("Publisher");
                            this.isManager = this.uid.equals(detail.getNString("Manager"));
                        }
                    }
                } else { //user
                    try (
                        PreparedStatement detailStat = new SelectQuery().select("*").from("Users").where("UID=?", uid).toStatement(con);
                        ResultSet detail = detailStat.executeQuery()
                    ) {
                        if (detail.next())
                            this.balance = (long)(detail.getDouble("Balance")*1000);
                    }
                }
            } else {
                throw new InvalidUIDException();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Account && uid.equals(((Account)obj).getUid());
    }

    //是否拥有某个app的seller权限
    public boolean hasSellerPermission(String appid) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("select * from Applications where AppID = ? and Publisher = ?")
        ) {
            stat.setNString(1, appid);
            stat.setNString(2, publisher);
            try (ResultSet res = stat.executeQuery()) {
                return res.next();
            }
        }
    }

    //查询某个用户是否具有一个app的MainPlan所有权
    public boolean hasBoughtApp(String appid) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = new SelectQuery().select("*").from("PossessesEx")
                                        .where("AppID=?", appid).where("UID=?", uid)
                                        .where("IsMainPlan=1").toCountStatement(con);
            ResultSet res = stat.executeQuery()
        ) {
            return res.next();
        }
    }

}
