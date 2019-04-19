package me.maiz.trainning.dal.mapper;

import me.maiz.trainning.dal.model.Book;

import java.util.List;
import java.util.Map;

public interface BookMapperCustom extends BookMapper {

    List<Book> querySelective(Map<String,Object> params);

    int countSelective(Map<String,Object> params);

}