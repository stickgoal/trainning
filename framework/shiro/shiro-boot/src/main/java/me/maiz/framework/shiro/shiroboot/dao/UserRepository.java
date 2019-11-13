package me.maiz.framework.shiro.shiroboot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.maiz.framework.shiro.shiroboot.entity.User;
import org.apache.ibatis.annotations.Select;

public interface UserRepository  extends BaseMapper<User> {
    @Select("select * from user where username=#{username}")
    User findByUsername(String username);
}
