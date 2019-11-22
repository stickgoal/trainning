package com.woniuxy.boot.ssmboot.dao;

import com.woniuxy.boot.ssmboot.entity.Perm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Lucas
 * @since 2019-11-14
 */
public interface PermMapper extends BaseMapper<Perm> {

    Set<String> findPermInRoleIds(Set<Integer> roleIds);

}
