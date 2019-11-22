package com.woniuxy.boot.ssmboot.service.impl;

import com.woniuxy.boot.ssmboot.entity.User;
import com.woniuxy.boot.ssmboot.dao.UserMapper;
import com.woniuxy.boot.ssmboot.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
