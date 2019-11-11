package me.maiz.framework.boot.ssmboot.dal;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.maiz.framework.boot.ssmboot.entity.User;
import me.maiz.framework.boot.ssmboot.entity.UserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

}