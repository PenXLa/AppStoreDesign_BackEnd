package kernel;

import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private String email;
    private String username = null;
    private String role = null;
    private String publisher = null;//如果role为seller，此变量存储其所在公司
    private long balance = 0; //余额，单位为0.1分

    public String getEmail() { return email; }
    public String getName() { return username; }
    public String getRole() { return role; }
    public String getPublisher() { return publisher; }
    public long getBalance() { return balance; }

    public Account(String email) {
        this.email = email;
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = con.prepareStatement("select Name, Role from Logins where Email = ?");
        ) {
            stat.setNString(1, email);
            try (ResultSet res = stat.executeQuery()) {
                if (res.next()) {
                    this.username = res.getNString("Name");
                    this.role = res.getString("Role");
                    if ("seller".equals(this.role)) {
                        try (PreparedStatement detailStat = con.prepareStatement("select * from Sellers where Email=?")) {
                            detailStat.setNString(1, email);
                            try (ResultSet detail = detailStat.executeQuery()) {
                                if (detail.next()) {
                                    this.publisher = detail.getNString("Publisher");
                                }
                            }
                        }
                    } else { //user
                        try (PreparedStatement detailStat = con.prepareStatement("select * from Users where Email=?")) {
                            detailStat.setNString(1, email);
                            try (ResultSet detail = detailStat.executeQuery()) {
                                if (detail.next()) {
                                    this.balance = (long)(detail.getDouble("Balance")*1000);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Account && email.equals(((Account)obj).getEmail());
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
            PreparedStatement stat = con.prepareStatement("select * from PossessesEx where AppID=? and UID=? And IsMainPlan=1")
        ) {
            stat.setNString(1, appid);
            stat.setNString(2, email);
            try (ResultSet res = stat.executeQuery()) {
                return res.next();
            }
        }
    }

}
