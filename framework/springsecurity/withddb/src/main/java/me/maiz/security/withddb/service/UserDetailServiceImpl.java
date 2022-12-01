package me.maiz.security.withddb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import me.maiz.security.withddb.mapper.UserMapper;
import me.maiz.security.withddb.model.OwnUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;

@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<OwnUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username",username);
        OwnUser user = userMapper.selectOne(userQueryWrapper);

        return new User(user.getUsername(),user.getPassword(), Arrays.asList());
    }
}
