package me.maiz.trainning.framework.spring.aop.proxy;

/**
 * Created by Lucas on 2017-01-12.
 */
public class UserServiceImpl implements UserService {

    public void deleteUser(int id) {
        System.out.println("真的删除了用户");
    }

}
