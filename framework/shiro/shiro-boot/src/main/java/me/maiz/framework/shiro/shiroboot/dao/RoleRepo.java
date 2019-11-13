package me.maiz.framework.shiro.shiroboot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.maiz.framework.shiro.shiroboot.entity.Role;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

public interface RoleRepo extends BaseMapper<Role> {

    @Select("select * from role r,user_role ru where r.role_id=ru.role_id and ru.user_id=#{userId}")
    Set<Role> findByUserId(int userId);


}
