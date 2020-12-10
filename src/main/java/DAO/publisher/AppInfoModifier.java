package DAO.publisher;

import VO.AppDetailVO;
import utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class AppInfoModifier {
    public static void setEnabled(String appid, boolean enabled) throws SQLException, ClassNotFoundException {
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("update Applications set Active=? where AppID=?");
        ) {
            stat.setBoolean(1, enabled);
            stat.setNString(2, appid);
            stat.executeUpdate();
        }
    }
    //还没有完成Tag修改功能
    //没有验证权限，权限验证在servlet进行
    public static void setBasicInfo(String appid, AppDetailVO vo) throws SQLException, ClassNotFoundException {
        StringBuffer sql = new StringBuffer();
        Queue<String> params = new LinkedList<>();
        if (vo.CPU != null) {
            sql.append("Req_CPU=?,");
            params.add(vo.CPU);
        }
        if (vo.GPU != null) {
            sql.append("Req_GPU=?,");
            params.add(vo.GPU);
        }
        if (vo.hardDisk != null) {
            sql.append("Req_HardDisk=" + vo.hardDisk + ",");
        }
        if (vo.introduction != null) {
            sql.append("Introduction=?,");
            params.add(vo.introduction);
        }
        if (vo.name != null) {
            sql.append("Name=?,");
            params.add(vo.name);
        }
        if (vo.OS != null) {
            sql.append("Req_OS=?,");
            params.add(vo.OS);
        }
        if (vo.RAM != null) {
            sql.append("Req_RAM=" + vo.RAM + ",");
        }
        if (vo.req_other != null) {
            sql.append("Req_Other=?,");
            params.add(vo.req_other);
        }
        if (vo.site != null) {
            sql.append("OfficalSite=?,");
            params.add(vo.site);
        }
        if (vo.version != null) {
            sql.append("Version=?,");
            params.add(vo.version);
        }
        if (vo.active != null) {
            sql.append("Active=" + (vo.active?"1":"0") + ",");
        }
        if (sql.length() > 0) sql.deleteCharAt(sql.length()-1); //删除最后一个逗号
        try (
            Connection con = Utils.connectDB("AppStoreDesign");
            PreparedStatement stat = con.prepareStatement("update Applications set " + sql.toString() + " where AppID=?");
        ) {
            int inx = 1;
            while (!params.isEmpty()) {
                stat.setNString(inx, params.poll());
                ++inx;
            }
            stat.setNString(inx++, appid);
            stat.executeUpdate();

            //tag修改
            if (vo.tags != null) {
                try (PreparedStatement clearTags = con.prepareStatement("delete from AppTags where AppID = ?")) {
                    clearTags.setNString(1, appid);
                    clearTags.executeUpdate();
                    for (String tag : vo.tags) {
                        try (PreparedStatement insertTag = con.prepareStatement("insert into AppTags (AppID, Tag) values (?,?)")) {
                            insertTag.setNString(1, appid);
                            insertTag.setNString(2, tag);
                            insertTag.executeUpdate();
                        }
                    }
                }
            }

        }
    }
}
