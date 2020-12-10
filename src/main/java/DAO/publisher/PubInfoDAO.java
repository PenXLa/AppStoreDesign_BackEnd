package DAO.publisher;

import VO.publisher.PubInfoVO;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class PubInfoDAO {
    public static class PublisherNotFoundException extends Exception{}
    public static PubInfoVO getPubInfo(String email) throws SQLException, ClassNotFoundException, PublisherNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("select * from Publishers P, Sellers S where S.Publisher=P.PubID and Email = ?")
        ) {
            stat.setNString(1, email);
            try (ResultSet res = stat.executeQuery()) {
                if (res.next()) {
                    PubInfoVO ret = new PubInfoVO();
                    ret.name = res.getNString("PName");
                    return ret;
                } else throw new PublisherNotFoundException();
            }
        }
    }

    public static void setPubInfo(String email, PubInfoVO info) throws SQLException, ClassNotFoundException, PublisherNotFoundException {
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
                    PreparedStatement stat = con.prepareStatement("update Publishers set " + sql.toString() + " where PubID = (select Publisher from Sellers where Email=?)");
            ) {
                int inx = 1;
                while(!pars.isEmpty())
                    stat.setNString(inx++, pars.poll());
                stat.setNString(inx, email); //设置email
                int lines = stat.executeUpdate();
                if (lines == 0) throw new PublisherNotFoundException();
            }
        }
    }
}
