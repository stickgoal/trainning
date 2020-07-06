package me.maiz.trainning.demo.redisbootdemo.dao;

import me.maiz.trainning.demo.redisbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDAO extends JpaRepository<User,Integer> {

    @Query("update User set username=?1 where userId=?2")
    @Modifying
    void updateUsername(String username,int userId);

}
