package me.maiz.trainning.framework.mybatis;

import me.maiz.trainning.framework.mybatis.dal.entity.User;
import me.maiz.trainning.framework.mybatis.dal.entity.UserExample;
import me.maiz.trainning.framework.mybatis.dal.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        InputStream inputStream = null;

        try {

            inputStream = Resources.getResourceAsStream("mybatis-config.xml");

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            SqlSession sqlSession = sqlSessionFactory.openSession();

            UserMapper mapper = sqlSession.getMapper(UserMapper.class);

            User user = new User();
            user.setUserId(4L);
            user.setPassword("xasdf");
            user.setUsername("asdfadsf");
            user.setAge(123l);
            user.setBirthday(new Date());
            mapper.insert(user);

            mapper.deleteByPrimaryKey(4l);

            UserExample example = new UserExample();
            example.createCriteria().andAgeBetween(10l, 30l).andUsernameLike("王").andPasswordIsNotNull();
            mapper.selectByExample(example);
            sqlSession.commit();
            sqlSession.close();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
