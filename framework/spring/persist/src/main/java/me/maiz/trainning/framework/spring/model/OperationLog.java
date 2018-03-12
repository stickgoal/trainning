package me.maiz.trainning.framework.spring.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Lucas on 2018-03-09.
 */
@Entity
@Table(name="pesist_op_log")
public class OperationLog {

    @Id
    private long logId;

    private String userId;

    private String operation;

    private Date  operationTime;

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OperationLog{");
        sb.append("logId=").append(logId);
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", operation='").append(operation).append('\'');
        sb.append(", operationTime=").append(operationTime);
        sb.append('}');
        return sb.toString();
    }
}
