package DAO;

import VO.AppSearchItem;
import kernel.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AppSearcher {
    public static ArrayList<AppSearchItem> search(String name, String dev, int count, int page, List<String> tags, double lowRat, double highRat, double lowPri, double highPri, int lowSell, int highSell, String order, String orderby) throws SQLException, ClassNotFoundException {
        //生成SQL语句-------------------------------------------
        ConcurrentLinkedQueue<String> par = new ConcurrentLinkedQueue<>(); //SQL的参数
        StringBuffer sql = new StringBuffer("select AppID, Name, Volume, LowPrice, Rating from (select *, row_number() over(order by ");
        if ("def".equals(orderby)) {
            sql.append("(Volume-LowPrice+Rating)");
        } else if ("sell".equals(orderby)) {
            sql.append("Volume");
        } else if ("price".equals(orderby)) {
            sql.append("LowPrice");
        } else if ("rating".equals(orderby)) {
            sql.append("rating");
        }
        sql.append(" " + order + ") rownum from AppSearchInfo where ");

        sql.append(String.format("Rating Between %f and %f", lowRat, highRat));
        sql.append(String.format(" AND LowPrice Between %f and %e", lowPri, highPri));
        sql.append(String.format(" AND Volume Between %d and %d", lowSell, highSell));
        if (name != null) {
            sql.append(" AND Name like ?");
            par.offer(name);
        }
        if (dev != null) {
            sql.append(" AND Dev=?");
            par.offer(dev);
        }
        sql.append(String.format(") as tmp where rownum between %d and %d", count*(page-1)+1, count*page));

        //标签过滤
        //还没有实现

        ArrayList<AppSearchItem> items = new ArrayList<>();
        Connection con = Utils.connectDB("AppStoreDesign");
        PreparedStatement stat = con.prepareStatement(sql.toString());
        for (int i=1; !par.isEmpty(); ++i)
            stat.setNString(i, par.poll());

        ResultSet res = stat.executeQuery();
        while(res.next()) { //遍历App
            AppSearchItem item = new AppSearchItem();
            item.id = res.getNString("AppID");
            item.name = res.getNString("Name");
            item.price = res.getDouble("LowPrice");
            item.rating = res.getDouble("Rating");
            //获取Tag
            PreparedStatement tagQuery = con.prepareStatement("select Tag from AppTags where AppID = ?");
            tagQuery.setNString(1, item.id);
            ResultSet tagRes = tagQuery.executeQuery();
            StringBuffer sb = new StringBuffer();
            while(tagRes.next())
                sb.append(tagRes.getNString(1) + "|");
            sb.deleteCharAt(sb.length()-1);
            item.tags = sb.toString();
            tagRes.close();
            tagQuery.close();

            items.add(item);
        }

        res.close();
        stat.close();
        con.close();
        return items;
    }
}
