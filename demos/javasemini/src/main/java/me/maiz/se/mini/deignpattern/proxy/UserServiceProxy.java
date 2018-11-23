package me.maiz.se.mini.deignpattern.proxy;

/**
 * 远程用户服务代理，用调用方在同一应用中
 */
public class UserServiceProxy implements UserService {
    private RemoteUserService remoteUserService = new RemoteUserService();
    @Override
    public boolean login(String username, String password) {
        System.out.println("rpc,远程调用RemoteUserService,这里用本地服务模拟");
        return remoteUserService.login(username,password);
    }
}
