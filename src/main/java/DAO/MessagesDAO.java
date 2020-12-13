package DAO;

import VO.MessageVO;
import kernel.Account;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MessagesDAO {
    //获取某个用户的消息
    public static Pair<Integer, MessageVO[]> getMessages(Account receiver, boolean unreadOnly,int page, int pageSize) throws SQLException, ClassNotFoundException {
        SelectQuery sql = new SelectQuery();
        sql.select("M.*, Name AS SenderName").from("Messages M INNER JOIN Users U ON M.Sender=U.UID")
                    .where("Receiver=?", receiver.getUid())
                    .orderBy("Date desc").paginate(page, pageSize);
        if (unreadOnly) sql.where("Unread=1");
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = sql.toStatement(con);
            ResultSet res = stat.executeQuery();
        ) {
            ArrayList<MessageVO> messages = new ArrayList<>();
            while(res.next()) {
                MessageVO msg = new MessageVO();
                msg.date = res.getTimestamp("Date");
                msg.content = res.getNString("Content");
                msg.senderID = res.getNString("Sender");
                msg.senderName = res.getNString("SenderName");
                msg.unread = res.getBoolean("Unread");
                msg.title = res.getNString("Title");
                msg.mid = res.getString("MID");
                messages.add(msg);
            }
            return new ImmutablePair<>(sql.getResultTotalSize(con), messages.toArray(new MessageVO[0]));
        }
    }

    //设置已读状态
    public static void setReadStat(Account user, String mid, boolean read) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("update Messages set Unread=? where mid=? and receiver=?");
        ) {
            stat.setBoolean(1, !read);
            stat.setString(2, mid);
            stat.setString(3, user.getUid());
            stat.executeUpdate();
        }
    }

    public static void delete(Account user, String mid) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("delete from Messages where mid=? and receiver=?");
        ) {
            stat.setString(1, mid);
            stat.setString(2, user.getUid());
            stat.executeUpdate();
        }
    }
}
