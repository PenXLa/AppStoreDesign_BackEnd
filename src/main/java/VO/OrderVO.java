package VO;

import java.sql.Timestamp;

public class OrderVO {
    public String OID = null;
    public String appid, appname, planid, planname;
    public boolean isMainPlan = false;
    public Timestamp date;
    public long price;
    public String description;
    public short status = 1; //订单状态
}
