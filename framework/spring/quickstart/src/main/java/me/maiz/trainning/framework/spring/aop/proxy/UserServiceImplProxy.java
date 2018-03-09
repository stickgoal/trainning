package me.maiz.trainning.framework.spring.aop.proxy;

/**
 * Created by Lucas on 2017-01-12.
 */
public class UserServiceImplProxy implements UserService {

    private UserServiceImpl userServiceImpl = new UserServiceImpl();

    public void deleteUser(int id) {
        System.out.println("========== 警告，有人打算删除用户："+id+" ==========");
        userServiceImpl.deleteUser(id);
        System.out.println("========== 警告，用户["+id+"]已经被删除 ==========");
    }
}
