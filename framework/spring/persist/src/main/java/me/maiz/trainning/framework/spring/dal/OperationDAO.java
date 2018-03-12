package me.maiz.trainning.framework.spring.dal;

import me.maiz.trainning.framework.spring.model.OperationLog;

/**
 * Created by Lucas on 2018-03-09.
 */
public interface OperationDAO {

    void save(OperationLog log);

}
