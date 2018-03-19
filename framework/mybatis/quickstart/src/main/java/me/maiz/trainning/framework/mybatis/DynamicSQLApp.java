package me.maiz.trainning.framework.mybatis;

import me.maiz.trainning.framework.mybatis.dal.mapper.BlogMapper;
import me.maiz.trainning.framework.mybatis.dal.model.Blog;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 动态SQL例子
 * Created by Lucas on 2018-03-19.
 */
public class DynamicSQLApp {
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";

    public static void main(String[] args) {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(MYBATIS_CONFIG);

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            SqlSession sqlSession = sqlSessionFactory.openSession();

            //动态参数 if
            BlogMapper blogMapper = sqlSession.getMapper(BlogMapper.class);


            List<Blog> blogs = blogMapper.findActiveBlogWithTitleLike("哈哈");
            System.out.println("有参数："+blogs);
            System.out.println("无参数："+blogMapper.findActiveBlogWithTitleLike(null));

            System.out.println("有参数：(\"如何\",\"Lucas\")"+blogMapper.findActiveBlogLike("如何","Lucas"));
            System.out.println("有参数：(null,\"Lucas\")"+blogMapper.findActiveBlogLike(null,"Lucas"));
            System.out.println("无参数：(null,null)"+blogMapper.findActiveBlogLike(null,null));

            //choose-when-otherwise
            System.out.println("有参数：(\"如何\",\"Lucas\")"+blogMapper.findActiveBlogLikeOr("如何","Lucas"));
            System.out.println("有参数：(null,\"Lucas\")"+blogMapper.findActiveBlogLikeOr(null,"Lucas"));
            System.out.println("无参数：(null,null)"+blogMapper.findActiveBlogLikeOr(null,null));

            //where
            System.out.println("无参数：(null,null)"+blogMapper.findActiveBlogLikeWhere(null,null));
            System.out.println("无参数：(null,null)"+blogMapper.findActiveBlogLikeWhere2(null,null));

            //set
            Blog blog = new Blog();
            blog.setBlogId(1);
            blog.setAuthorName("Miles");
            blogMapper.updateBlogIfNecessary(blog);

            Blog b1 = new Blog();
            b1.setBlogId(1);
            b1.setState("INACTIVE");
            blogMapper.updateBlogIfNecessary(b1);


            Blog b2 = new Blog();
            b2.setBlogId(1);
            b2.setState("INACTIVE");
            b2.setAuthorName("Alfred");
            blogMapper.updateBlogIfNecessary(b2);




            sqlSession.commit();

            sqlSession.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
