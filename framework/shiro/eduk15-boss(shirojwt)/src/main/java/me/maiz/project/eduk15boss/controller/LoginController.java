package me.maiz.project.eduk15boss.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.project.eduk15boss.common.JwtUtils;
import me.maiz.project.eduk15boss.common.Result;
import me.maiz.project.eduk15boss.dao.OperatorMapper;
import me.maiz.project.eduk15boss.model.Operator;
import me.maiz.project.eduk15boss.model.OperatorExample;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Slf4j
public class LoginController {


    @Autowired
    private OperatorMapper operatorMapper;


    @PostMapping("login")
    public Result login(HttpServletResponse response, String username, String password){
        //执行登录操作，签发jwt ,但不走shiro
        log.info("登录操作：{},{}",username,password);

        OperatorExample example = new OperatorExample();
        example.createCriteria()
                .andOperatorNameEqualTo(username)
                .andPasswordEqualTo(password);
        List<Operator> operators = operatorMapper.selectByExample(example);

        Operator user = operators.get(0);
        if(user==null||operators.size()>1){
            return Result.fail("用户"+username+"不存在或者密码不正确");
        }


        //响应里返回jwt
        String jwt = JwtUtils.createJWT(user);
        response.setHeader(JwtUtils.AUTH_TOKEN_NAME,jwt);

        return Result.success(jwt);
    }


}
