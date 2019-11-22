package com.woniuxy.boot.ssmboot.dao;

import com.woniuxy.boot.ssmboot.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Lucas
 * @since 2019-11-14
 */
public interface RoleMapper extends BaseMapper<Role> {

    Set<Role> findRolesByUserId(int userId);

}
