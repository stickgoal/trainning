package me.maiz.shiro.customlizerealm;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyRealm extends AuthorizingRealm {


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //得到用户id



        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        simpleAuthorizationInfo.addRole("manager");
        simpleAuthorizationInfo.addStringPermission("fire");
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //得到用户名和密码
        UsernamePasswordToken upt = (UsernamePasswordToken) token;
        String username = upt.getUsername();

        //访问数据库
        try {
            User user = findUser(username);
            //将数据返回给Shiro
            return new SimpleAuthenticationInfo(username,user.getPassword(),"myRealm");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AuthenticationException("数据库访问出错;"+e.getMessage(),e);
        }
    }

    public User findUser(String username) throws SQLException {
        Connection conn = JDBCUtils.getConnection();
        QueryRunner qr = new QueryRunner();
        User user = qr.query(conn, "select * from users where username=?", new BeanHandler<User>(User.class), username);
        return user;
    }

}
