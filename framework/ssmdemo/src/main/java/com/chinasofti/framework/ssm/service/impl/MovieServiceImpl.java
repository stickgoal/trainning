package com.chinasofti.framework.ssm.service.impl;

import com.chinasofti.framework.ssm.dao.MovieDAO;
import com.chinasofti.framework.ssm.dao.model.Movie;
import com.chinasofti.framework.ssm.service.MovieService;
import com.chinasofti.framework.ssm.service.info.MovieInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

@Service("movieService")
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieDAO movieDAO;

    @Override
    public List<Movie> search(String director, String protagonist, Date releaseDateBegin, Date releaseDateEnd) {
        List<Movie> movies = movieDAO.search(director,protagonist,releaseDateBegin,releaseDateEnd);
        return movies;
    }

    @Override
    public void add(MovieInfo info) {
        Movie movie = new Movie();
        BeanUtils.copyProperties(info,movie);
        movieDAO.insert(movie);
    }

    @Override
    public void delete(int movieId) {
        movieDAO.deleteByPrimaryKey(movieId);
    }

    @Override
    public Movie load(int movieId) {
        return movieDAO.selectByPrimaryKey(movieId);
    }

    @Override
    public void modify(MovieInfo info) {
        Movie movie = new Movie();
        BeanUtils.copyProperties(info,movie);
        movieDAO.updateByPrimaryKeySelective(movie);
    }


    @Autowired
    private TransactionTemplate txTemplate;

    @Override
    public void test() {
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //编程式事务管理
                Movie movie = new Movie();
                movie.setDirect("x");
                movie.setProtagonist("sd");
                movie.setReleaseDate(new Date());
                movie.setLanguage("x");
                movie.setMovieName("sdfsd");

                movieDAO.insert(movie);
                if(movie.getMovieId()>0){
                    throw new RuntimeException();
                }
                movieDAO.deleteByPrimaryKey(movie.getMovieId()-1);
            }
        });






    }

    //声明式事务管理
    @Override
    public void test2() {

        doInTx();

    }

    @Transactional(rollbackFor = Exception.class)
    public void doInTx() {
        Movie movie = new Movie();
        movie.setDirect("x");
        movie.setProtagonist("sd");
        movie.setReleaseDate(new Date());
        movie.setLanguage("x");
        movie.setMovieName("sdfsd");

        movieDAO.insert(movie);
        if (movie.getMovieId() > 0) {
            throw new RuntimeException();
        }
        movieDAO.deleteByPrimaryKey(movie.getMovieId() - 1);
    }


}
