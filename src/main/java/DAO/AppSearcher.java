package DAO;

import VO.AppSearchResult;
import VO.UserAppVO;
import kernel.Account;
import org.apache.commons.lang3.tuple.Pair;
import utils.SelectQuery;
import utils.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AppSearcher {
    //publisher是筛选指定的开发商拥有的app
    //user是筛选指定的用户拥有的app
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
                    item.icon = Utils.getAppIconURL(item.id, res.getString("IconType"));
                    item.tags = AppTagDAO.getAppTags(item.id, con);//获取Tag
                    item.lastUpdate = res.getTimestamp("LastUpdate");
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
                    item.icon = Utils.getAppIconURL(item.id, res.getString("IconType"));
                    item.tags = AppTagDAO.getAppTags(item.id, con);//获取Tag
                    item.active = res.getBoolean("Active");
                    item.lastUpdate = res.getTimestamp("LastUpdate");
                    item.version = res.getNString("Version");

                    searchResult.items.add(item);
                }
                return searchResult;
            }
        }
    }





}
