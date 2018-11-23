package me.maiz.se.mini.deignpattern.proxy;

/**
 * 模拟的远程用户服务，实际上是部署在另一台机器上
 */
public class RemoteUserService implements UserService {
   @Override
    public boolean login(String username, String password) {
    System.out.println("远程服务验证");
    return false;
    }
}
