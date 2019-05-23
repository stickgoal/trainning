package me.maiz.trainning.framework.mybatis;

import me.maiz.trainning.framework.mybatis.dal.entity.Movie;
import me.maiz.trainning.framework.mybatis.dal.entity.MovieExample;
import me.maiz.trainning.framework.mybatis.dal.mapper.MovieMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

            MovieMapper mapper = sqlSession.getMapper(MovieMapper.class);
            Movie m = new Movie();
            m.setDirect("吴京");
            m.setLanguage("中文");
            m.setMovieName("战狼2");
            m.setProtagonist("吴京、赵海洋");
            m.setReleaseDate(new Date());
            mapper.insertSelective(m);
            sqlSession.commit();
            System.out.println(m);

            mapper.deleteByPrimaryKey(1);
            sqlSession.commit();
            System.out.println(mapper.selectByPrimaryKey(1));

            Movie upMoive = new Movie();
            upMoive.setMovieId(5);
            upMoive.setMovieName("战狼3");
            mapper.updateByPrimaryKeySelective(upMoive);
            sqlSession.commit();

            MovieExample movieExample = new MovieExample();
            MovieExample.Criteria criteria = movieExample.createCriteria();
            Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-01-01 00:00:00");
            criteria.andProtagonistLike("吴%")
                    .andReleaseDateGreaterThan(date);
            //or
            MovieExample.Criteria criteria1 = movieExample.createCriteria();
            criteria1.andDirectIsNull();
            movieExample.or(criteria1);
            //order by
            movieExample.setOrderByClause(" release_date desc");
            //distinct
//            movieExample.isDistinct();


            List<Movie> movies = mapper.selectByExample(movieExample);
            System.out.println(movies);

            Movie upMovie1 = new Movie();
            upMovie1.setProtagonist("吴京");
            mapper.updateByExampleSelective(upMovie1,movieExample);
            sqlSession.commit();
            sqlSession.commit();
            sqlSession.close();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
