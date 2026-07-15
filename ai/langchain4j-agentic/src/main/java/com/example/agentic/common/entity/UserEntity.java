package com.example.agentic.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表实体，对应数据库 {@code user} 表。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class UserEntity {

    @TableId(type = IdType.INPUT)
    private String userId;

    private String name;

    /** VIP 等级：NORMAL / VIP / SVIP */
    private String vipLevel;

    private Integer orderCount;

    /** 历史退款次数 */
    private Integer refundCount;
}
