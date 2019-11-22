package com.woniuxy.boot.ssmboot.service.impl;

import com.woniuxy.boot.ssmboot.entity.Role;
import com.woniuxy.boot.ssmboot.dao.RoleMapper;
import com.woniuxy.boot.ssmboot.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lucas
 * @since 2019-11-14
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
