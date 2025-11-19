package me.maiz.shardingjdbcdemo.controller;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import me.maiz.shardingjdbcdemo.model.Balance;
import me.maiz.shardingjdbcdemo.model.UserLogin;
import me.maiz.shardingjdbcdemo.service.BalanceService;
import me.maiz.shardingjdbcdemo.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户登录表 前端控制器
 * </p>
 *
 * @author Lucas
 * @since 2025-11-17
 */
@RestController
@Slf4j
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private BalanceService balanceService;


    @RequestMapping("/newUser")
    public String login(String username) {
        log.info("username:{}", username);
        for (int i = 0; i < 10; i++) {
            UserLogin userLogin = new UserLogin();
            userLogin.setLoginName(username+i);
            userLogin.setLoginPassword("adsfadfad");
            userLogin.setUserStatus((byte)0);
            userLogin.setLoginMobile("1231232131232131");
            userLogin.setDefaultLoginType((byte)0);
            userLogin.setCreateTime(LocalDateTime.now());
            userLogin.setUpdateTime(LocalDateTime.now());
            userLoginService.save(userLogin);
        }
        return "处理完成";
    }

    @RequestMapping("/get")
    public List<UserLogin> get(String username) {
        List<UserLogin> logins = userLoginService.query().like("login_name", "%" + username + "%").list();
        return logins;
    }

    @RequestMapping("/newBalance")
    public String newBalance() {
        log.info("插入余额");
        for (int i = 0; i < 10; i++) {

            Balance balance = new Balance();
            //主键由系统指定
            balance.setBalance(new BigDecimal(i*10000));
            balanceService.save(balance);
        }

        return "处理完成";
    }

    @RequestMapping("/getBalance")
    public List<Balance> getBalance() {
        return balanceService.list();
    }
}
