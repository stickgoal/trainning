package com.example.agentic.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.agentic.common.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {
}
