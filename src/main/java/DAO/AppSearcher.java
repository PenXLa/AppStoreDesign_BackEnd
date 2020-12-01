package DAO;

import VO.AppSearchResult;
import kernel.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AppSearcher {
    public static AppSearchResult search(String name, String publisher, int count, int page, List<String> tags, double lowRat, double highRat, double lowPri, double highPri, int lowSell, int highSell, String order, String orderby) throws SQLException, ClassNotFoundException {
        //生成SQL语句-------------------------------------------
        ConcurrentLinkedQueue<String> par = new ConcurrentLinkedQueue<>(); //SQL的参数
        StringBuffer sql = new StringBuffer("(select *, row_number() over(order by ");
        if ("def".equals(orderby)) {
            sql.append("(Volume-Price+Rating)");
        } else if ("sell".equals(orderby)) {
            sql.append("Volume");
        } else if ("price".equals(orderby)) {
            sql.append("Price");
        } else if ("rating".equals(orderby)) {
            sql.append("rating");
        }
        sql.append(" " + order + ") rownum from AppSearchInfo where Active=1 AND ");

        sql.append(String.format("Rating Between %f and %f", lowRat, highRat));
        sql.append(String.format(" AND Price Between %f and %e", lowPri, highPri));
        sql.append(String.format(" AND Volume Between %d and %d", lowSell, highSell));
        if (name != null && !"".equals(name)) {
            sql.append(" AND Name like ?");
            par.offer("%" + name + "%");
        }
        if (publisher != null && !"".equals(publisher)) {
            sql.append(" AND Publisher=?");
            par.offer(publisher);
        }
        sql.append(") as tmp");

        //标签过滤
        //还没有实现

        AppSearchResult searchResult = new AppSearchResult();
        searchResult.items = new ArrayList<>();
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("select * from " + sql.toString() + String.format(" where rownum between %d and %d", count*(page-1)+1, count*page));
            PreparedStatement countStat = con.prepareStatement("select count(*) from " + sql.toString());
        ) {
            for (int i=1; !par.isEmpty(); ++i) {
                countStat.setNString(i, par.peek());
                stat.setNString(i, par.poll());
            }
            try (ResultSet res = stat.executeQuery()) {
                while(res.next()) { //遍历App
                    AppSearchResult.AppSearchItem item = new AppSearchResult.AppSearchItem();
                    item.id = res.getNString("AppID");
                    item.name = res.getNString("Name");
                    item.price = res.getDouble("Price");
                    item.oriprice = res.getDouble("OriginalPrice");
                    item.rating = res.getDouble("Rating");
                    item.iconType = res.getString("IconType");
                    item.tags = AppTagDAO.getAppTags(item.id, con);//获取Tag


                    searchResult.items.add(item);
                }

                try (ResultSet countRes = countStat.executeQuery()) {
                    countRes.next();
                    searchResult.total = countRes.getInt(1);
                    searchResult.curPage = page;
                    return searchResult;
                }
            }
        }
    }

    //开发商自己的app，没有搜索参数，不分页
    public static AppSearchResult publisherSearch(String publisher) throws SQLException, ClassNotFoundException {
        AppSearchResult searchResult = new AppSearchResult();
        searchResult.items = new ArrayList<>();
        try (
                Connection con = Utils.connectDB("AppStoreDesign");
                PreparedStatement stat = con.prepareStatement("select * from AppSearchInfo where Publisher=?");
        ) {
            stat.setNString(1, publisher);
            try (ResultSet res = stat.executeQuery()) {
                while(res.next()) { //遍历App
                    AppSearchResult.AppSearchItem item = new AppSearchResult.AppSearchItem();
                    item.id = res.getNString("AppID");
                    item.name = res.getNString("Name");
                    item.price = res.getDouble("Price");
                    item.oriprice = res.getDouble("OriginalPrice");
                    item.rating = res.getDouble("Rating");
                    item.iconType = res.getString("IconType");
                    item.tags = AppTagDAO.getAppTags(item.id, con);//获取Tag
                    item.active = res.getBoolean("Active");

                    searchResult.items.add(item);
                }
                return searchResult;
            }
        }
    }
}
