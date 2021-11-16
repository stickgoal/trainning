package me.maiz.trainning.framework.spring.aop.declaretivetx;

import java.sql.Connection;

public class TransactionHolder {

    private static final ThreadLocal<Connection> TX_HOLDER = new ThreadLocal<Connection>();

    public static void set(Connection connection){
        TX_HOLDER.set(connection);
    }

    public static Connection get(){
        return TX_HOLDER.get();
    }

}
