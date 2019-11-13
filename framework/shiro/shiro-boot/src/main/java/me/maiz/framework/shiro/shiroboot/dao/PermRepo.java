package me.maiz.framework.shiro.shiroboot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.maiz.framework.shiro.shiroboot.entity.Permission;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

public interface PermRepo extends BaseMapper<Permission> {

    @Select("select p.* from permission p,role_permission rp,role r,user_role ur where p.perm_id=rp.perm_id and r.role_id=rp.role_id and ur.role_id=r.role_id and ur.user_id=#{userId}")
    Set<Permission> findByUserId(int userId);

}
