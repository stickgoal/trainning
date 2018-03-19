package me.maiz.trainning.framework.mybatis;

import me.maiz.trainning.framework.mybatis.dal.mapper.UserMapper;
import me.maiz.trainning.framework.mybatis.dal.model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Hello world!
 *
 */
public class BasicApp {
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";
    public static void main(String[] args) {


//        accessWithXml(MYBATIS_CONFIG);
        accessWithInterface(MYBATIS_CONFIG);
    }

    /**
     * 用xml方式访问
     * @param resource
     */
    private static void accessWithInterface(String resource) {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            SqlSession sqlSession = sqlSessionFactory.openSession();
            //类名和xml中的namespace相对应，接口方法和xml中的id相对应
            //mybatis自动映射在一起
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);


            User user =  userMapper.findByUserId(1);
            System.out.println(user);

            userMapper.deleteUser(2);

            //- 新增
            User userInsert = new User();
            userInsert.setUserId(2);
            userInsert.setUsername("王二");
            userInsert.setAge(12);
            userInsert.setPassword("axhasdfa");
            userMapper.insertUser(userInsert);

            User userUpdate = new User();
            userUpdate.setUserId(2);
            userUpdate.setAge(120);
            userUpdate.setUsername("sdfasdf");
            userMapper.updateUser(userUpdate);

            System.out.println(userMapper.findAll());

            sqlSession.commit();

            sqlSession.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用XML 方式访问
     * @param resource
     */
    private static void accessWithXml(String resource) {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            SqlSession sqlSession = sqlSessionFactory.openSession();
            //- 查找单个
            //使用xml中的namespace 和 id拼接成statementId来定位SQL语句，第二参数为sql的参数
            User user = sqlSession.selectOne("quickstart.mappers.user.findByUserId",1);
            System.out.println(user);

            //- 删除
            sqlSession.delete("quickstart.mappers.user.deleteUser",2);

            //- 新增
            User userInsert = new User();
            userInsert.setUserId(2);
            userInsert.setUsername("王二");
            userInsert.setAge(12);
            userInsert.setPassword("axhasdfa");
            sqlSession.insert("quickstart.mappers.user.insertUser",userInsert);
            sqlSession.commit();

            //- 更新
            User userUpdate = new User();
            userUpdate.setUserId(2);
            userUpdate.setAge(120);
            userUpdate.setUsername("sdfasdf");
            sqlSession.update("quickstart.mappers.user.updateUser",userUpdate);

            //- 查找全部
            List<User> allUsers =  sqlSession.selectList("quickstart.mappers.user.findAll");
            System.out.println(allUsers);

            sqlSession.commit();
            sqlSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
