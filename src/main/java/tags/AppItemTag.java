package tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class AppItemTag extends SimpleTagSupport {
    private String appid, appname;
    private double price, oriprice, rating;
    private String tags;

    public void setAppname(String appname) {
        this.appname = appname;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setOriprice(double oriprice) {
        this.oriprice = oriprice;
    }
    public void setAppid(String appid) {
        this.appid = appid;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }




    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        out.print(String.format(
                "<div class='appFrame'>" +
                "      <div style=\"text-align: center\"><img src='images/icons/%s.png' width='150' height='150'></div>" +
                "      <p><a href='apps/%s.html' class='appLink'>%s</a></p>" +
                "      <p class='appRating'>评价：%.1f</p>" +
                "      <p style='margin:10px 0px 10px 0px'>" +
                "           <span>￥%.2f</span>" +
                "           <s %s class=\"oriprice\">￥%.2f</s>" +
                "      </p>" +
                "      <div class='tagDiv'>"
            , appid, appid, appname, rating, price, oriprice <= price ? "style='display:none'":"" , oriprice)
        );



        if (tags != null) {
            String[] tagList = tags.split("\\|");
            for (String tag : tagList)
                out.print(String.format("<span class='apptag'>%s</span>", tag));
        }

        out.println("</div></div>");
    }
}
