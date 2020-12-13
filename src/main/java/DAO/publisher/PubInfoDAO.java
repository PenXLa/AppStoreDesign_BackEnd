package DAO.publisher;

import VO.publisher.PubInfoVO;
import kernel.Account;
import utils.SelectQuery;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class PubInfoDAO {
    public static class PublisherNotFoundException extends Exception{}
    public static PubInfoVO getPubInfo(Account seller) throws SQLException, ClassNotFoundException, PublisherNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = new SelectQuery().select("*").from("Publishers P INNER JOIN Sellers S ON S.Publisher=P.PubID")
                                        .where("UID=?", seller.getUid()).toStatement(con);
            ResultSet res = stat.executeQuery()
        ) {
            if (res.next()) {
                PubInfoVO ret = new PubInfoVO();
                ret.name = res.getNString("PName");
                return ret;
            } else throw new PublisherNotFoundException();
        }
    }

    public static void setPubInfo(Account seller, PubInfoVO info) throws SQLException, ClassNotFoundException, PublisherNotFoundException {
        StringBuffer sql = new StringBuffer();
        Queue<String> pars = new LinkedList<String>(); //参数队列
        if (info.name!=null) { //生成sql语句
            sql.append("PName=?,");
            pars.add(info.name);
        }
        if (sql.length()>0) {
            sql.deleteCharAt(sql.length()-1); //删去最后一个逗号
            try (
                    Connection con = Utils.connectDB("AppStoreDesign");
                    PreparedStatement stat = con.prepareStatement("update Publishers set " + sql.toString() + " where PubID = (select Publisher from Sellers where UID=?)");
            ) {
                int inx = 1;
                while(!pars.isEmpty()) stat.setNString(inx++, pars.poll()); //把队列中的参数送入查询
                stat.setNString(inx, seller.getUid()); //设置查询中的UID
                int lines = stat.executeUpdate();
                if (lines == 0) throw new PublisherNotFoundException();
            }
        }
    }
}
