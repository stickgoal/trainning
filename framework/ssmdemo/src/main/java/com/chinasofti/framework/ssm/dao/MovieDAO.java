package com.chinasofti.framework.ssm.dao;

import com.chinasofti.framework.ssm.dao.model.Movie;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface MovieDAO extends MovieMapper {

    List<Movie> search(@Param("director")String director,
                       @Param("protagonist")String protagonist,
                       @Param("releaseDateBegin")Date releaseDateBegin,
                       @Param("releaseDateEnd")Date releaseDateEnd);


}
