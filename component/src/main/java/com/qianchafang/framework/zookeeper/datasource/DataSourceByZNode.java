package com.qianchafang.framework.zookeeper.datasource;

import com.qianchafang.framework.zookeeper.ZookeeperHolder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Poan on 2017/5/9.
 */
public class DataSourceByZNode extends ZkBasedDataSource {

    // 数据库连接的相关配置信息，都同一存到该路径下
    private static final String CONFIG_FREFIX = "/qcf/dataSource/";

    private static final long PERIOD_SLEEP = TimeUnit.SECONDS.toMillis(30);

    private DataSourceByZNode(String zNodeVal) {
        super(ZKPaths.makePath(CONFIG_FREFIX, zNodeVal), ZookeeperHolder::getInstance);
    }

    @Override
    protected long waitStopPeriod() {
        return PERIOD_SLEEP;
    }

    private static ConcurrentHashMap<String, DataSourceByZNode> dataSources = new ConcurrentHashMap<>();

    public static final DataSourceByZNode of(String bizName) {
        return dataSources.computeIfAbsent(bizName, DataSourceByZNode::new);
    }
}
