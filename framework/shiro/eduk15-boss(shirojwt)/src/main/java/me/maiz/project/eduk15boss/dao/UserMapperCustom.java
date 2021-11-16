package me.maiz.project.eduk15boss.dao;

import me.maiz.project.eduk15boss.model.User;
import me.maiz.project.eduk15boss.model.UserExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapperCustom extends UserMapper{

    @Select("select * from rkt_user where username like concat('%',#{username},'%') limit #{pageStart},#{pageSize}")
    public List<User> findByNamePage(@Param("username") String username,
                                     @Param("pageStart") int pageStart,
                                     @Param("pageSize") int pageSize);


}