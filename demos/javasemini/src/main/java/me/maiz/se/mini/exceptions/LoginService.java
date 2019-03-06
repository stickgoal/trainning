package me.maiz.se.mini.exceptions;

public class LoginService {

    public UserInfo login(String username, String password){
        UserInfo userInfo= queryDatabase(username,password);
        if(userInfo==null){
            throw new UsernameOrPasswordIncorrectException("用户名或密码不正确，请重新输入");
        }
        return userInfo;
    }

    private UserInfo queryDatabase(String username, String password) {
        return null;
    }

}
