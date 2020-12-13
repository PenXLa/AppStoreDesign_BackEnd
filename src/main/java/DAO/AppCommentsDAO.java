package DAO;

import VO.AppCommentVO;
import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppCommentsDAO {
    public enum Order {
        DATE_LATEST, DATE_OLDEST, RATING_HIGHEST, RATING_LOWEST
    }
    public static AppCommentVO[] getAppComments(String appid, Order order) throws SQLException, ClassNotFoundException {
        String orderby;
        if (order == Order.DATE_LATEST) {
            orderby = "Date desc";
        } else if (order == Order.DATE_OLDEST) {
            orderby = "Date asc";
        } else if (order == Order.RATING_HIGHEST) {
            orderby = "Rating desc";
        } else {
            orderby = "Rating asc";
        }
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = new SelectQuery().select("*").from("Users U INNER JOIN CommentsOn C ON U.UID=C.UID")
                                        .where("AppID = ?", appid)
                                        .orderBy(orderby).toStatement(con);
            ResultSet res = stat.executeQuery();
        ) {
            ArrayList<AppCommentVO> comments = new ArrayList<>();
            while(res.next()) {
                AppCommentVO comment = new AppCommentVO();
                comment.authorID = res.getNString("UID");
                comment.authorName = res.getNString("Name");
                comment.rating = res.getDouble("Rating");
                comment.content = res.getNString("Content");
                comment.date = res.getTimestamp("Date");
                comments.add(comment);
            }
            return comments.toArray(new AppCommentVO[0]);
        }
    }
}
