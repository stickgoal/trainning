package com.chinasofti.framework.ssm.service;

import com.chinasofti.framework.ssm.dao.model.Movie;
import com.chinasofti.framework.ssm.service.info.MovieInfo;

import java.util.Date;
import java.util.List;

public interface MovieService  {

    List<Movie>  search(String director, String protagonist, Date releaseDateBegin, Date releaseDateEnd);

    void add(MovieInfo info);

    void delete(int movieId);

    Movie load(int movieId);

    void modify(MovieInfo info);


    void test();

    void test2();
}
