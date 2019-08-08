package me.maiz.trainning.framework.spring.service;

import me.maiz.trainning.framework.spring.model.User;

/**
 * Created by Lucas on 2018-03-09.
 */
public interface UserService {

    void registerDeclaration(User user);

    void registerProgrammatic(User user);

    /**
     * 测试事务传播特性-PROPAGATION_REQUIRED
     * 当前有事务则加入，无事务则创建事务
     */
    void propagationRequired(User user);

    /**
     * PROPAGATION_REQUIRED_NEW
     * 暂停当前事务，创建新事务
     */
    void propagationRequiredNew(User user);

    /**
     * PROPAGATION_SUPPORTS
     * 有事务则加入事务，无事务则无事务运行
     */
    void propagationSupport(User user);

    /**
     * PROPAGATION_MANDATORY
     * 无事务则报错
     */
    void propagationRequiredMandatory(User user);


}
