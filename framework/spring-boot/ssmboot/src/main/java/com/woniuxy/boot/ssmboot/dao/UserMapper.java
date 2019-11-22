package com.woniuxy.boot.ssmboot.dao;

import com.woniuxy.boot.ssmboot.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Lucas
 * @since 2019-11-14
 */
public interface UserMapper extends BaseMapper<User> {

    User findByUsername(String username);

}
