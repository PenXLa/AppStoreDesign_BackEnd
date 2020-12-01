package servlets.apps;

import VO.AppSearchResult;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/search")
public class AppSearcher extends HttpServlet {
    private final int MAX_COUNT = 50;//一页最大结果数量
    /*
    * Search的参数有
    * name：名字，支持模糊搜索
    * publisher：开发商id
    * tag：拥有tag，多tag用|分隔
    * lowrating：评分低线
    * highrating：评分高线
    * lowprice：评分低线
    * highprice：评分高线
    * lowsell：交易量低线
    * highsell：交易量高线
    *
    * count：一页数量，最多50
    * page：页数
    *
    * orderby：排序方式，有：
    *   def综合
    *   sell交易量
    *   price价格
    *   rating评分
    *
    * order：升序/降序排列
    *   asc升序
    *   desc降序
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println(searchApp(req).toJSONString());
    }

    private JSONObject searchApp (HttpServletRequest req) {
        //读取URL参数------------------------------------------
        String pCount = req.getParameter("count");
        String pPage = req.getParameter("page");
        String pTag = req.getParameter("tag");
        String pLowRat = req.getParameter("lowrating");
        String pHighRat = req.getParameter("highrating");
        String pLowPri = req.getParameter("lowprice");
        String pHighPri = req.getParameter("highprice");
        String pLowSell = req.getParameter("lowsell");
        String pHighSell = req.getParameter("highsell");
        String pOrderBy = req.getParameter("orderby"); if (pOrderBy!=null) pOrderBy=pOrderBy.toLowerCase();
        String pOrder = req.getParameter("order"); if (pOrder!=null) pOrder=pOrder.toLowerCase();

        int count = MAX_COUNT, page = 1;
        String name = req.getParameter("name");
        String publisher = req.getParameter("publisher");
        ArrayList<String> tags = null;
        double lowRat = 0, highRat = 10;
        double lowPri = 0, highPri = 1e300;//1e300作为无穷大
        int lowSell = 0, highSell = Integer.MAX_VALUE;
        String orderby = "def", order = "desc";

        JSONObject res = new JSONObject();
        try {
            if (pCount != null) count = Math.min(Integer.parseInt(pCount), MAX_COUNT);
            if (pPage != null) page = Integer.parseInt(pPage);
            if (pTag != null)
                tags = new ArrayList<>(Arrays.asList(pTag.split("\\|")));
            if (pLowRat != null) lowRat = Double.parseDouble(pLowRat);
            if (pHighRat != null) highRat = Double.parseDouble(pHighRat);
            if (pLowPri != null) lowPri = Double.parseDouble(pLowPri);
            if (pHighPri != null) highPri = Double.parseDouble(pHighPri);
            if (pLowSell != null) lowSell = Integer.parseInt(pLowSell);
            if (pHighSell != null) highSell = Integer.parseInt(pHighSell);
            if (pOrderBy != null && pOrderBy.matches("(def|sell|price|rating)")) orderby = pOrderBy;
            if (pOrder != null && pOrder.matches("(asc|desc)")) order = pOrder;
        } catch (RuntimeException e) {
            res.put("success", false);
            res.put("reason", "Wrong parameter format.");
            return res;
        }

        try {
            AppSearchResult searchResult = DAO.AppSearcher.search(name, publisher, count, page, tags, lowRat, highRat, lowPri, highPri,lowSell, highSell, order, orderby);
            res.put("searchResult", searchResult);
            res.put("success", true);
        } catch (SQLException | ClassNotFoundException e) {
            res.put("success", false);
            res.put("resaon", "DB Error");
            e.printStackTrace();
        }
        return res;

    }
}
