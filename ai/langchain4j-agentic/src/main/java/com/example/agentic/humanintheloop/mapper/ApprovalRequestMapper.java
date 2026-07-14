package com.example.agentic.humanintheloop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.agentic.humanintheloop.entity.ApprovalRequestEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人工审批请求 Mapper。状态持久化到 {@code approval_request} 表，
 * 替代原实现里内存 {@code Map<String, Future<String>>} 的易失存储。
 */
@Mapper
public interface ApprovalRequestMapper extends BaseMapper<ApprovalRequestEntity> {
}
