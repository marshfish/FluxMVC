package com.fluxMVC.core.initialize;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/10
 */
public final class TransactionHandler {
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    /**
     * 开始事务
     */
    public static void beginTransaction() {
        Connection connection = getConnection();
        if (null != connection) {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }
    }

    /**
     * 提交事务
     *
     * @return
     */
    public static void commitTransaction() {
        Connection connection = getConnection();
        if (null != connection) {
            try {
                connection.commit();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 回滚事务
     *
     * @return
     */
    public static void rollbackTransaction() {
        Connection connection = getConnection();
        if (null != connection) {
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 获取连接
     *
     * @return
     */
    private static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(Config.getJDBCDriver());
            connection = DriverManager.getConnection(Config.getJDBCURL(), Config.getJDBCUsername(), Config.getJDBCPassword());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
