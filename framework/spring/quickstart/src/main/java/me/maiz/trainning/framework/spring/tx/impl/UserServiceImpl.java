package me.maiz.trainning.framework.spring.tx.impl;

import me.maiz.trainning.framework.spring.tx.UserDAO;
import me.maiz.trainning.framework.spring.tx.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;


    @Transactional
    public void doSth(int userId, String username) {
        userDAO.insert("asdfasd");
        int x = userId/0;
        userDAO.update(userId,username);
    }

    @Autowired
    private TransactionTemplate txTemplate;

    public void doSth2(final int userId, final String username) {
        txTemplate.execute(new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                userDAO.insert("asdfasd");
                int x = userId/0;
                userDAO.update(userId,username);
            }
        });
    }
}
