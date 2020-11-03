package kernel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private String email;
    private String username = null;
    public String getEmail() { return email; }
    public String getName() { return username; }

    public Account(String email) {
        this.email = email;
        Connection con = null;
        try {
            con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("select Name from Users where Email = ?");
            stat.setNString(1, email);
            ResultSet res = stat.executeQuery();
            if (res.next())
                this.username = res.getNString("Name");

            res.close();
            stat.close();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Account && email.equals(((Account)obj).getEmail());
    }
}
