package VO;

import java.sql.Timestamp;

public class ReturnVO {
    public String rid;
    public String oid;
    public String appid, appname, planid, planname;
    public boolean isMainPlan = false;
    public Timestamp buyDate, applyDate;
    public long price;
    public String description;
    public String uid, userName;
    public String reason;
}
