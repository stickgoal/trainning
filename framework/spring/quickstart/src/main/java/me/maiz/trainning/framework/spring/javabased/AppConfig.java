package me.maiz.trainning.framework.spring.javabased;

import me.maiz.trainning.framework.spring.javabased.impl.UserDAOImpl;
import me.maiz.trainning.framework.spring.javabased.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserDAO userDAO(){
        UserDAOImpl dao = new UserDAOImpl();
        dao.setUsername("lucas");
        return dao;
    }

    @Bean
    public UserServiceImpl userService(UserDAO dao){
        UserServiceImpl userService =  new UserServiceImpl();
        userService.setUserDAO(dao);
        return userService;
    }


}
