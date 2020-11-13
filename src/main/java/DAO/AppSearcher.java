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
        StringBuffer sql = new StringBuffer("select * from (select *, row_number() over(order by ");
        if ("def".equals(orderby)) {
            sql.append("(Volume-Price+Rating)");
        } else if ("sell".equals(orderby)) {
            sql.append("Volume");
        } else if ("price".equals(orderby)) {
            sql.append("Price");
        } else if ("rating".equals(orderby)) {
            sql.append("rating");
        }
        sql.append(" " + order + ") rownum from AppSearchInfo where ");

        sql.append(String.format("Rating Between %f and %f", lowRat, highRat));
        sql.append(String.format(" AND Price Between %f and %e", lowPri, highPri));
        sql.append(String.format(" AND Volume Between %d and %d", lowSell, highSell));
        if (name != null) {
            sql.append(" AND Name like ?");
            par.offer("%" + name + "%");
        }
        if (dev != null) {
            sql.append(" AND Dev=?");
            par.offer(dev);
        }
        sql.append(String.format(") as tmp where rownum between %d and %d", count*(page-1)+1, count*page));

        //标签过滤
        //还没有实现

        ArrayList<AppSearchItem> items = new ArrayList<>();
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement(sql.toString())
        ) {
            for (int i=1; !par.isEmpty(); ++i)
                stat.setNString(i, par.poll());
            try (ResultSet res = stat.executeQuery()) {
                while(res.next()) { //遍历App
                    AppSearchItem item = new AppSearchItem();
                    item.id = res.getNString("AppID");
                    item.name = res.getNString("Name");
                    item.price = res.getDouble("Price");
                    item.oriprice = res.getDouble("OriginalPrice");
                    item.rating = res.getDouble("Rating");
                    item.iconType = res.getString("IconType");
                    item.tags = AppTagDAO.getAppTags(item.id, con);//获取Tag

                    items.add(item);
                }
                return items;
            }
        }
    }
}
