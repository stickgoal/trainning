package me.maiz.trainning.framework.spring.aop.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class DBHelper {

    @Autowired
    private DataSource druidDataSource;


    /*
     * 获取连接
     */
    public Connection getConnection() {
        Connection con = null;
        try {
            con = druidDataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("连接获取失败");
            e.printStackTrace();
        }
        return con;
    }

    /*
     * 归还连接
     */

    public void close(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println("连接归还失败");
            e.printStackTrace();
        }
    }

    public int update(String sql, Object... params) {
        Connection conn = getConnection();

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
            close(conn);
        }
    }


}
