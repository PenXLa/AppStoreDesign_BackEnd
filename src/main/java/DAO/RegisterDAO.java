package DAO;

import utils.Utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class RegisterDAO {
    public static class EmailUsedException extends Exception{}
    public static class WrongInvitationException extends Exception{}
    public static void register(String email, String name, String passhash) throws SQLException, ClassNotFoundException, EmailUsedException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                CallableStatement call = con.prepareCall("{ call UserRegister(?, ?, ?) }")
        ) {
            call.setNString("email", email);
            call.setNString("name", name);
            call.setString("passhash", passhash);
            call.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 50006) throw new EmailUsedException();
            else throw e;
        }
    }

    public static void registerPublisher(String email, String name, String passhash, String pubName) throws SQLException, ClassNotFoundException, EmailUsedException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                CallableStatement call = con.prepareCall("{ call PublisherRegister(?, ?, ?, ?) }")
        ) {
            call.setNString("email", email);
            call.setNString("name", name);
            call.setString("passhash", passhash);
            call.setNString("pubname", pubName);
            call.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 50006) throw new EmailUsedException();
            else throw e;
        }
    }

    public static void register(String email, String name, String passhash, String invitation) throws SQLException, ClassNotFoundException, EmailUsedException, WrongInvitationException {
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                CallableStatement call = con.prepareCall("{ call RegisterWithInvitation(?, ?, ?, ?) }")
        ) {
            call.setNString("email", email);
            call.setNString("name", name);
            call.setString("passhash", passhash);
            call.setString("code", invitation);
            call.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 50006) throw new EmailUsedException();
            else if (e.getErrorCode() == 50010) throw new WrongInvitationException();
            else throw e;
        }
    }
}
