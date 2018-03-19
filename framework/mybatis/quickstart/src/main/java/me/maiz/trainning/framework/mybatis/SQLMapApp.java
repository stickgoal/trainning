package me.maiz.trainning.framework.mybatis;

import me.maiz.trainning.framework.mybatis.dal.mapper.UserMapper;
import me.maiz.trainning.framework.mybatis.dal.model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sqlMap配置的使用
 * Created by Lucas on 2018-03-19.
 */
public class SQLMapApp {
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";

    public static void main(String[] args) {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(MYBATIS_CONFIG);

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            SqlSession sqlSession = sqlSessionFactory.openSession();

            //参数为Map
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            Map<String,Object> params = new HashMap<>();
            params.put("username","王二");
            params.put("userId",1);
            List<User> userList = userMapper.findByMap(params);
            System.out.println(userList);

            //结果为Map
            Map<String,Object> resultData = userMapper.findResultMap();
            System.out.println(resultData);

            //sql标签的使用
            User userUpdate = new User();
            userUpdate.setUserId(1);
            userUpdate.setAge(120);
            userUpdate.setUsername("sdfasdf");
            userUpdate.setPassword("xxxxx");
            userMapper.updateUserById(userUpdate);
            userUpdate.setAge(111);
            userMapper.updateUserByUsername(userUpdate);

            sqlSession.commit();

            sqlSession.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
