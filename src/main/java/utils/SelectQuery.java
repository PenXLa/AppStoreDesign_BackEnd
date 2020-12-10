package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SelectQuery {
    private String cols, fromTables;
    private static class WhereCondition {
        String cond;
        Object[] params;
        public WhereCondition(String cond, Object[] params) {
            this.cond = cond;
            this.params = params;
        }
    }
    private ArrayList<WhereCondition> conds = new ArrayList<>();
    private String orderClause = null;
    private Integer page = null, size = null; //分页用


    public SelectQuery select(String cols) {
        this.cols = cols;
        return this;
    }
    public SelectQuery from(String fromTables) {
        this.fromTables = fromTables;
        return this;
    }
    //调用之后增加一个条件。params是PreparedStatement里的?的值
    public SelectQuery where(String cond, Object... params) {
        conds.add(new WhereCondition(cond, params));
        return this;
    }

    public SelectQuery orderBy(String exp) {
        orderClause = exp;
        return this;
    }

    public SelectQuery paginate(int page, int size) {
        this.page = page;
        this.size = size;
        return this;
    }


    public PreparedStatement toStatement(Connection con) throws SQLException {
        Queue<Object> params = new LinkedList<>();
        StringBuffer sql = new StringBuffer("select " + cols + " from " + fromTables);
        if (!conds.isEmpty()) {
            sql.append(" where ");
            for (int i=0; i<conds.size(); ++i) {
                sql.append(conds.get(i).cond);
                for (Object param : conds.get(i).params)
                    params.add(param);
                if (i < conds.size() - 1) sql.append(" AND ");
            }
        }
        if (orderClause != null) {
            sql.append(" order by ");
            sql.append(orderClause);
            if (page != null && size != null)
                sql.append(paginateTSQL(page, size));
        }


        PreparedStatement stat = con.prepareStatement(sql.toString());
        for (int i=1; !params.isEmpty(); ++i) {
            stat.setObject(i, params.poll());
        }
        return stat;
    }


    //用于查询分页前的总结果数
    public PreparedStatement toCountStatement(Connection con) throws SQLException {
        Queue<Object> params = new LinkedList<>();
        StringBuffer sql = new StringBuffer("select count(*) from " + fromTables);
        if (!conds.isEmpty()) {
            sql.append(" where ");
            for (int i=0; i<conds.size(); ++i) {
                sql.append(conds.get(i).cond);
                for (Object param : conds.get(i).params)
                    params.add(param);
                if (i < conds.size() - 1) sql.append(" AND ");
            }
        }


        PreparedStatement stat = con.prepareStatement(sql.toString());
        for (int i=1; !params.isEmpty(); ++i) {
            stat.setObject(i, params.poll());
        }
        return stat;
    }

    public static String paginateTSQL(int page, int size) {
        return String.format(" offset %d rows fetch next %s rows only ", (page-1)*size, size);
    }

}
