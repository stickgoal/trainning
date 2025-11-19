package me.maiz.shardingjdbcdemo.service.impl;

import me.maiz.shardingjdbcdemo.model.UserLogin;
import me.maiz.shardingjdbcdemo.mapper.UserLoginMapper;
import me.maiz.shardingjdbcdemo.service.UserLoginService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录表 服务实现类
 * </p>
 *
 * @author Lucas
 * @since 2025-11-17
 */
@Service
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLogin> implements UserLoginService {

}
