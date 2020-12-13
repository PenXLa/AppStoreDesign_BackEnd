package VO;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class AppSearchResult {
    public static class AppSearchItem {
        public String name;
        public String id;
        public double rating;
        public double price, oriprice;
        public String[] tags;
        public String icon;
        public Boolean active = null; //null可以防止不必要时被json序列化
        public Timestamp lastUpdate = null;
        public String version = null;
    }

    public ArrayList<AppSearchItem> items;
    public int total = 0, curPage = 0; //total是结果总数
}
