package com.woniuxy.servelet02.mapper;

import com.woniuxy.servelet02.entity.Product;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper {

    @Select("select * from wn_product where description like concat('%','${keyword}','%')")
    List<Product> findAll(String keyword);

}
