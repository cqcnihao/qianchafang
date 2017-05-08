package com.qianchafang.framework.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Poan on 2017/5/8.
 */
public class ZookeeperHolder {
    private static CuratorFramework INSTANCE;
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static CuratorFramework getInstance() {
        if (INSTANCE == null) {
            try {
                lock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = CuratorFrameworkFactory.newClient(
                            "this is a secret", new ExponentialBackoffRetry(10000, 3));
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }


}


