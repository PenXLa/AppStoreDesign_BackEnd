package DAO.custom_service;

import utils.Utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BackupDAO {
    public static void backup() throws SQLException, ClassNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = con.prepareStatement("BACKUP DATABASE AppStoreDesign TO DISK = '/var/opt/mssql/data/AppStoreDesign.bak' WITH MEDIANAME = 'SQLServerBackups', NAME = 'Full Backup of AppStoreDesign'");
        ) {
            stat.executeUpdate();
        }
    }

    public static void restore() throws SQLException, ClassNotFoundException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = con.prepareStatement("RESTORE DATABASE AppStoreDesign FROM DISK = '/var/opt/mssql/data/AppStoreDesign.bak' WITH NORECOVERY;");
        ) {
            stat.executeUpdate();
        }
    }
}
