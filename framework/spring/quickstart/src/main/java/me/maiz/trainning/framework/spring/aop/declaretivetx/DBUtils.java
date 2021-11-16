package me.maiz.trainning.framework.spring.aop.declaretivetx;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    static{
        init("mysql:jdbc://localhost:3306/woniu_edu","root","root123","com.mysql.jdbc.Driver");
    }

    private static DruidDataSource dds = null;

    public static void init(String url, String username, String password, String driverClassName) {

        dds = new DruidDataSource();
        dds.setDriverClassName(driverClassName);
        dds.setUrl(url);
        dds.setUsername(username);
        dds.setPassword(password);
    }

    /*
     * 获取连接
     */
    public static Connection getConnection() {
        Connection con = null;
        try {
            con = dds.getConnection();
        } catch (SQLException e) {
            System.err.println("连接获取失败");
            e.printStackTrace();
        }
        return con;
    }

    /*
     * 归还连接
     */

    public static void close(Connection con) {
        try {
            if(con!=null) {
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("连接归还失败");
            e.printStackTrace();
        }
    }

    public int update(Connection conn,String sql, Object... params) {

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setObject(i, params[i - 1]);
            }
            System.out.println("执行更新语句："+pstmt);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("sql异常", e);
        } finally {
            DBUtils.close(conn);
        }
    }

    public <T> List<T> selectList(Connection conn ,String sql, Adapter<T> adapter, Object... params) {
        List<T> resultList = new ArrayList<T>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setObject(i, params[i - 1]);
            }
            System.out.println("执行查询语句："+pstmt);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                resultList.add(adapter.adapt(rs));
            }
            return resultList;
        } catch (SQLException e) {
            throw new IllegalStateException("sql异常", e);
        } finally {
            DBUtils.close(conn);
        }
    }

    public <T> T selectOne(Connection conn,String sql, Adapter<T> adapter, Object... params) {
        List<T> resultList = selectList(conn,sql, adapter, params);
        if (resultList.size() == 0) {
            return null;
        }
        if (resultList.size() != 1) {
            throw new IllegalStateException("结果个数不是1，共" + resultList.size() + "个");
        }
        return resultList.get(0);
    }

    public static interface Adapter<T> {

        T adapt(ResultSet rs);
    }




}
