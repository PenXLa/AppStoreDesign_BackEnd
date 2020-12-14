package utils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class UpdateQuery {
    private String table;
    private ArrayList<Pair<String, Object[]>> conds = new ArrayList<>(), sets = new ArrayList<>();

    public UpdateQuery update(String table) {
        this.table = table;
        return this;
    }
    public UpdateQuery set(String col, Object... values) {
        sets.add(new ImmutablePair<String, Object[]>(col, values));
        return this;
    }
    //调用之后增加一个条件。params是PreparedStatement里的?的值
    public UpdateQuery where(String cond, Object... params) {
        conds.add(new ImmutablePair<String, Object[]>(cond, params));
        return this;
    }


    public PreparedStatement toStatement(Connection con) throws SQLException {
        Queue<Object> params = new LinkedList<>();
        StringBuffer sql = new StringBuffer("update " + table + " set ");
        for (int i=0; i<sets.size(); ++i) {
            sql.append(sets.get(i).getLeft());
            for (Object param : sets.get(i).getRight()) params.add(param);
            if (i < sets.size() - 1) sql.append(" , ");
        }

        if (!conds.isEmpty()) {
            sql.append(" where ");
            for (int i=0; i<conds.size(); ++i) {
                sql.append(conds.get(i).getLeft());
                for (Object param : conds.get(i).getRight()) params.add(param);
                if (i < conds.size() - 1) sql.append(" AND ");
            }
        }

        PreparedStatement stat = con.prepareStatement(sql.toString());
        for (int i=1; !params.isEmpty(); ++i) {
            stat.setObject(i, params.poll());
        }
        return stat;
    }

}
