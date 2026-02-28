package com.woniuxy.servelet02.util;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MybatisUtil {

    public static SqlSession session;

    public static SqlSession getSession(boolean autoCommit, String realPath) throws FileNotFoundException {
        if (session == null) {
            System.out.println(realPath);
            SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(new FileInputStream(realPath+ "/mybatis-config.xml"));
            //设置该连接为自动提交（默认手动提交）
            session = ssf.openSession(autoCommit);
        }
        return session;
    }

    public static void closeSession() {
        if (session != null) {
            session.close();
            session = null;
        }
    }

}
