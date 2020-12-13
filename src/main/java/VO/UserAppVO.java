package VO;

import java.sql.Date;
import java.sql.Timestamp;

public class UserAppVO {
    public static class SubscribeItem {
        public String planid;
        public String name;
        public String explanation;
        public Timestamp expireDate;
    }

    public String name;
    public String appid;
    public String[] tags;
    public String icon;
    public Timestamp lastUpdate = null;
    public String version = null;
    public SubscribeItem[] subscribeItems;
}
