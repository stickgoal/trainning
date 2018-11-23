package me.maiz.se.mini.deignpattern.proxy;

public class Consumer {

    private  UserService userService = new UserServiceProxy();

    public void userLogin(String username,String password){
        userService.login(username,password);
    }


}
