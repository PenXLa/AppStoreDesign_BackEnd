package DAO;

import VO.AppSearchResult;
import utils.SelectQuery;
import utils.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AppSearcher {
    //publisher是筛选指定的开发商拥有的app
    //user是筛选指定的用户拥有的app
//    public static AppSearchResult search(String name, String publisher, String user, int count, int page, List<String> tags, double lowRat, double highRat, double lowPri, double highPri, int lowSell, int highSell, String order, String orderby) throws SQLException, ClassNotFoundException {
//        //生成SQL语句-------------------------------------------
//        ConcurrentLinkedQueue<String> par = new ConcurrentLinkedQueue<>(); //SQL的参数
//        StringBuffer sql = new StringBuffer("select * from AppSearchInfo CROSS APPLY (SELECT COUNT(*) Count FROM AppSearchInfo ) Count where Active=1 AND ");
//        sql.append(String.format("Rating Between %f and %f", lowRat, highRat));
//        sql.append(String.format(" AND Price Between %f and %e", lowPri, highPri));
//        sql.append(String.format(" AND Volume Between %d and %d", lowSell, highSell));
//        if (name != null && !"".equals(name)) {
//            sql.append(" AND Name like ?");
//            par.offer("%" + name + "%");
//        }
//        if (publisher != null && !"".equals(publisher)) { //查某个开发商的app
//            sql.append(" AND Publisher=?");
//            par.offer(publisher);
//        }
//        if (user != null && !"".equals(user)) { //查某个用户的app
//            sql.append(" AND AppID in (select AppID from PossessesEx where UID=? AND IsMainPlan=1)");
//            par.offer(user);
//        }
//        sql.append(" order by ");
//
//        if ("def".equals(orderby)) {
//            sql.append("(Volume-Price+Rating)");
//        } else if ("sell".equals(orderby)) {
//            sql.append("Volume");
//        } else if ("price".equals(orderby)) {
//            sql.append("Price");
//        } else if ("rating".equals(orderby)) {
//            sql.append("Rating");
//        }
//        sql.append(Utils.paginateTSQL(page, count));
//
//        //标签过滤
//        //还没有实现
//
//        AppSearchResult searchResult = new AppSearchResult();
//        searchResult.items = new ArrayList<>();
//        try (
//                Connection con = Utils.connectDB("AppStoreDesign");
//                PreparedStatement stat = con.prepareStatement(sql.toString());
//        ) {
//            for (int i=1; !par.isEmpty(); ++i) {
//                stat.setNString(i, par.poll());
//            }
//            try (ResultSet res = stat.executeQuery()) {
//                if (res.next()) {
//                    searchResult.total = res.getInt("Count"); //每一行都有count字段，且都相同。这里是从第一行读出来count
//                    searchResult.curPage = page;
//                    do { //遍历App
//                        AppSearchResult.AppSearchItem item = new AppSearchResult.AppSearchItem();
//                        item.id = res.getNString("AppID");
//                        item.name = res.getNString("Name");
//                        item.price = res.getDouble("Price");
//                        item.oriprice = res.getDouble("OriginalPrice");
//                        item.rating = res.getDouble("Rating");
//                        item.iconType = res.getString("IconType");
//                        item.tags = AppTagDAO.getAppTags(item.id, con);//获取Tag
//                        item.lastUpdate = res.getDate("LastUpdate");
//                        item.version = res.getNString("Version");
//
//                        searchResult.items.add(item);
//                    } while(res.next());
//                }
//                return searchResult;
//            }
//        }
//    }

    public static AppSearchResult search(String name, String publisher, String user, int count, int page, List<String> tags, double lowRat, double highRat, double lowPri, double highPri, int lowSell, int highSell, String order, String orderby) throws SQLException, ClassNotFoundException {
        //生成SQL语句-------------------------------------------
        SelectQuery sql = new SelectQuery();
        sql.select("*").from("AppSearchInfo");
        sql.where(String.format("Rating Between %f and %f", lowRat, highRat));
        sql.where(String.format("Price Between %f and %e", lowPri, highPri));
        sql.where(String.format("Volume Between %d and %d", lowSell, highSell));
        if (name != null && !"".equals(name))
            sql.where("Name like ?", "%" + name + "%");
        if (publisher != null && !"".equals(publisher))  //查某个开发商的app
            sql.where("Publisher=?", publisher);
        if (user != null && !"".equals(user))  //查某个用户的app
            sql.where("AppID in (select AppID from PossessesEx where UID=? AND IsMainPlan=1)", user);

        if ("def".equals(orderby)) {
            sql.orderBy("(Volume-Price+Rating) " + order);
        } else if ("sell".equals(orderby)) {
            sql.orderBy("Volume " + order);
        } else if ("price".equals(orderby)) {
            sql.orderBy("Price " + order);
        } else if ("rating".equals(orderby)) {
            sql.orderBy("Rating " + order);
        } else if ("name".equals(orderby)) {
            sql.orderBy("Name " + order);
        }
        sql.paginate(page, count);

        //标签过滤
        //还没有实现

        AppSearchResult searchResult = new AppSearchResult();
        searchResult.items = new ArrayList<>();
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = sql.toStatement(con);
            PreparedStatement countStat = sql.toCountStatement(con);
        ) {
            try (
                ResultSet countRes = countStat.executeQuery();
                ResultSet res = stat.executeQuery()
            ) {
                countRes.next();
                searchResult.total = countRes.getInt(1);
                searchResult.curPage = page;
                while (res.next()) {
                    AppSearchResult.AppSearchItem item = new AppSearchResult.AppSearchItem();
                    item.id = res.getNString("AppID");
                    item.name = res.getNString("Name");
                    item.price = res.getDouble("Price");
                    item.oriprice = res.getDouble("OriginalPrice");
                    item.rating = res.getDouble("Rating");
                    item.iconType = res.getString("IconType");
                    item.tags = AppTagDAO.getAppTags(item.id, con);//获取Tag
                    item.lastUpdate = res.getDate("LastUpdate");
                    item.version = res.getNString("Version");

                    searchResult.items.add(item);
                }
                return searchResult;
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
                    item.lastUpdate = res.getDate("LastUpdate");
                    item.version = res.getNString("Version");

                    searchResult.items.add(item);
                }
                return searchResult;
            }
        }
    }
}
