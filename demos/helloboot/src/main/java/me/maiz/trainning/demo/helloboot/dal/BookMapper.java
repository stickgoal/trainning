package me.maiz.trainning.demo.helloboot.dal;

import me.maiz.trainning.demo.helloboot.dal.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookMapper {

    @Select("Select * from book where id=#{id}")
    public Book findById(int id);

}
