package VO;

import java.util.Date;

public class AppDetailVO {
    public String id, name;
    public long size;
    public String version;
    public long RAM, hardDisk;
    public String CPU, GPU, OS, req_other;
    public String dev; //开发商名字。前端不需要开发商的ID
    public Date lastUpdate;
    public double rating;
    public String site;
    public String iconType;
    public String introduction;
    public String tags;
    public static class AppPlan {
        public String id;
        public String name;
        public String explanation;
        public double price, oriprice;
        public int duration;
        public boolean isMain;
    }
    public AppPlan mainPlan;
    public AppPlan[] subscribePlans;
}
