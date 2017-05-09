package com.qianchafang.framework.zookeeper.datasource;

import com.qianchafang.framework.util.ObjectMapperUtils;
import com.qianchafang.framework.zookeeper.AbstractLazyZkNode;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.curator.framework.CuratorFramework;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Created by Poan on 2017/5/8.
 */
public class ZkBasedDataSource extends AbstractLazyZkNode<BasicDataSource> implements DataSource {

    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("Ops.", e);
            throw new RuntimeException(e);
        }
    }

    public ZkBasedDataSource(String monitorPath, Supplier<CuratorFramework> clientFactory) {
        super(monitorPath, clientFactory);
    }

    @Override
    protected BasicDataSource initObject(String zkValue) {
        try {
            // 将zNode的存贮的json转为Map
            Map<String, Object> node = ObjectMapperUtils.fromJSON(zkValue, Map.class, String.class,
                    Object.class);
            // 连接数据库
            String url = (String) node.get("url");
            String user = (String) node.get("user");
            String pass = (String) node.get("pass");
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl(url);
            dataSource.setUsername(user);
            dataSource.setPassword(pass);

            // 连接池配置
            dataSource.setMinIdle(1);
            dataSource.setMaxIdle(10);
            dataSource.setMaxTotal(-1);
            dataSource.setDefaultAutoCommit(true);
            dataSource.setMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(1));
            dataSource.setSoftMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(1));
            dataSource.setTestOnBorrow(true);
            dataSource.setTestWhileIdle(true);
            dataSource.setValidationQuery("/* ping */");

            BeanUtils.populate(dataSource, node);
            logger.info("successful build datasource for {}, {}", monitorPath, url);
            return dataSource;
        } catch (Exception e) {
            logger.error("faild to build datasource,{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Predicate<BasicDataSource> cleanUpOperation() {
        return oldResource -> {
            if (oldResource.isClosed()) {
                return true;
            }
            try {
                oldResource.close();
                return oldResource.isClosed();
            } catch (SQLException e) {
                logger.error("faild to close old dataSource:{}", oldResource, e);
                return false;
            }
        };
    }


    // 以下都是实现BasicDataSource的方法
    @Override
    public Connection getConnection() throws SQLException {
        return getResource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getResource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getResource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getResource().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getResource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getResource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getResource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getResource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getResource().getParentLogger();
    }

}
