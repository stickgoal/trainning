package com.example.agentic.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.agentic.common.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
