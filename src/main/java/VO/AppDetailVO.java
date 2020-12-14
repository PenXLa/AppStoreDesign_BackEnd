package VO;

import java.sql.Timestamp;
import java.util.Date;

public class AppDetailVO {
    public String id, name, code;
    public Long size = null;
    public String version;
    public Long RAM = null, hardDisk = null;
    public String CPU, GPU, OS, req_other;
    public String publisher; //开发商名字(前端不需要开发商的ID)
    public Timestamp lastUpdate;
    public Double rating = null;
    public String site;
    public String icon;
    public String introduction;
    public String[] tags;
    public static class AppPlan {
        public String id;
        public String name;
        public String code;
        public String explanation;
        public Double price, oriprice;
        public Long duration;
        public Boolean isMain;
        public Boolean bought;
        public Boolean active;
        public Integer volume = null;
    }
    public AppPlan mainPlan;
    public AppPlan[] subscribePlans;
    public Boolean active = null;
}
