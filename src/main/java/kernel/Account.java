package kernel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private String email;
    private String username = null;
    private String role = null;
    private String publisher = null;//如果role为seller，此变量存储其所在公司

    public String getEmail() { return email; }
    public String getName() { return username; }
    public String getRole() { return role; }
    public String getPublisher() { return publisher; }

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
}
