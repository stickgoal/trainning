package me.maiz.trainning.framework.spring.dal.impl;

import me.maiz.trainning.framework.spring.dal.OperationDAO;
import me.maiz.trainning.framework.spring.model.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by Lucas on 2018-03-09.
 */
//@Repository("operationDAO")
public class OperationDAOImpl implements OperationDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void save(OperationLog log) {
        jdbcTemplate.update("insert into op_log(log_id,user_id,op,op_time) values(?,?,?,?)",log.getLogId(),log.getUserId(),log.getOperation(),log.getOperationTime());
    }
}
