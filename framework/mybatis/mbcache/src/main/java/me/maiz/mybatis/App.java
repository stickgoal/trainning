package me.maiz.mybatis;

import me.maiz.mybatis.entity.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        //读配置文件
        InputStream resourceAsStream = Resources.getResourceAsStream("mybatis-config.xml");
        //构建SQLSessionFactory
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        //创建SqlSession
        SqlSession sqlSession = sessionFactory.openSession();
        //===== 使用Mapper接口访问 ======
        //生成一个实现类，实现类通过SQLSession访问xml中特定的语句（全类名.方法名）
        //全类名  <=> namespace
        //方法名  <=> sql的id
        //参数与返回值必须对应
        UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

        System.out.println(userDAO.findById(2));
        System.out.println(userDAO.findById(2));
        System.out.println(userDAO.findById(2));

        sqlSession.commit();

        System.out.println("============");
        SqlSession newSession = sessionFactory.openSession();
        UserDAO mapper = newSession.getMapper(UserDAO.class);
        System.out.println(mapper.findById(2));
        System.out.println(mapper.findById(2));
        System.out.println(mapper.findById(2));
        sqlSession.commit();


    }
}
