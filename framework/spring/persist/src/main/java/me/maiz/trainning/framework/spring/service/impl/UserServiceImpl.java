package me.maiz.trainning.framework.spring.service.impl;

import me.maiz.trainning.framework.spring.dal.OperationDAO;
import me.maiz.trainning.framework.spring.dal.UserDAO;
import me.maiz.trainning.framework.spring.model.OperationLog;
import me.maiz.trainning.framework.spring.model.User;
import me.maiz.trainning.framework.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * Created by Lucas on 2018-03-09.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OperationDAO operationDAO;

    @Autowired
    private TransactionTemplate txTemplate;

    @Transactional
    public void registerDeclaration(User user) {
        userDAO.save(user);
        if(user.getUserId()!=null) {
            throw new RuntimeException();
        }
        OperationLog opLog = new OperationLog();
        opLog.setLogId(1L);
        opLog.setOperation("注册");
        opLog.setUserId(user.getUserId());
        opLog.setOperationTime(new Date());
        operationDAO.save(opLog);
    }

    public void registerProgrammatic(final User user) {

        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    userDAO.save(user);
                    if (user.getUserId() != null) {
                        throw new RuntimeException();
                    }
                    OperationLog opLog = new OperationLog();
                    opLog.setLogId(2L);
                    opLog.setOperation("注册");
                    opLog.setUserId(user.getUserId());
                    opLog.setOperationTime(new Date());
                    operationDAO.save(opLog);
                }catch (Exception e){
                    e.printStackTrace();
                    //手动控制回滚
                    status.setRollbackOnly();
                }
            }
        });
    }

    @Transactional
    public void propagationRequired(User user) {
        userDAO.save(user);
        doSth1();
    }

    @Transactional
    public void doSth1(){
        throw new RuntimeException("required");
    }

    public void propagationRequiredNew(User user) {

    }

    public void propagationSupport(User user) {

    }

    public void propagationRequiredMandatory(User user) {

    }

}
