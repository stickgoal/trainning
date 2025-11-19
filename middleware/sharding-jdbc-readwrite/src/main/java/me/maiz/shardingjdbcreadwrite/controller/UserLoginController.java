package me.maiz.shardingjdbcreadwrite.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.shardingjdbcreadwrite.model.Balance;
import me.maiz.shardingjdbcreadwrite.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private BalanceService balanceService;


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
