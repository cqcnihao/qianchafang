package com.qianchafang.framework.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.NodeCache;

import java.util.function.Supplier;


/**
 * Created by Poan on 2017/5/8.
 */
public abstract class AbstractLazyZkNode<T> extends AbstractZkNode<T> {

    protected final String monitorPath;

    private final CuratorFramework client;

    private final Supplier<CuratorFramework> clientFactory;

    private volatile NodeCache cache;

    private AbstractLazyZkNode(String monitorPath, CuratorFramework client,
                               Supplier<CuratorFramework> clientFactory) {
        this.monitorPath = monitorPath;
        this.client = client;
        this.clientFactory = clientFactory;
    }

    public AbstractLazyZkNode(String monitorPath, Supplier<CuratorFramework> clientFactory) {
        this(monitorPath, null, clientFactory);
    }


    @Override
    protected NodeCache currentCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    CuratorFramework thisClient = null;
                    if (client != null) {
                        if (client.getState() != CuratorFrameworkState.STARTED) {
                            client.start();
                        }
                        thisClient = client;
                    }
                    if (clientFactory != null) {
                        thisClient = clientFactory.get();
                    }
                    if (thisClient == null) {
                        throw new RuntimeException(
                                "there is no curator framework or client factory found.");
                    }
                    NodeCache buildingCache = new NodeCache(thisClient, monitorPath);
                    try {
                        buildingCache.start();
                        buildingCache.rebuild();
                        this.cache = buildingCache;
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return cache;
    }


}
