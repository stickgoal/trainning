package me.maiz.framework.shiro.shiroboot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.maiz.framework.shiro.shiroboot.entity.Permission;
import me.maiz.framework.shiro.shiroboot.entity.RolePermission;

import java.util.Set;

public interface RolePermRepo extends BaseMapper<RolePermission> {

}
