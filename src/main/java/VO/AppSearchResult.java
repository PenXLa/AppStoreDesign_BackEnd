package VO;

import java.util.ArrayList;

public class AppSearchResult {
    public static class AppSearchItem {
        public String name;
        public String id;
        public double rating;
        public double price, oriprice;
        public String[] tags;
        public String iconType;
        public Boolean active = null; //null可以防止不必要时被json序列化
    }
    public ArrayList<AppSearchItem> items;
    public int total = 0, curPage = 0; //total是结果总数
}
