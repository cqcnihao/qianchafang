package com.qianchafang.framework.zookeeper;

import com.google.common.base.Optional;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * Created by Poan on 2017/5/8.
 * <descirption>
 * zookeeper于linux系统中可以创建一系列节点，而这些节点
 * 可以存储一定大小的数据；
 * 所以对节点进行操作时，要保证其线程安全性，以及节点修改后，
 * 及时通知到分布式中的其他服务器进行同步
 * </descirption>
 */
public abstract class AbstractZkNode<T> implements Closeable {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 由子类实现，将当前节点中存贮json数据传入，返回其泛型类型
     *
     * @param zkValue 节点中存贮的数据(统一以json形式保存)
     * @return 子类指定的泛型类型
     */
    protected abstract T initObject(String zkValue);

    protected abstract NodeCache currentCache();

    protected T emptyObject() {
        return null;
    }

    // 节点存储的数据 --> T
    protected T resource;

    // 节点的更新由lock保证其线程安全
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected T getResource() {
        // DCL
        if (resource == null) {
            try {
                lock.writeLock().lock();
                if (resource == null) {
                    ChildData currentData = currentCache().getCurrentData();
                    if (currentData == null || currentData.getData() == null) {
                        return emptyObject();
                    }
                    // 将当前节点中存储的json数据，转为子类自定的泛型类型
                    resource = initObject(new String(currentData.getData()));
                    currentCache().getListenable().addListener(() -> {
                        T oldResource = null;
                        synchronized (lock) {
                            ChildData data = currentCache().getCurrentData();
                            oldResource = resource;
                            if (data != null && data.getData() != null) {
                                resource = initObject(new String(data.getData()));
                            } else {
                                resource = emptyObject();
                            }
                            clean(oldResource);
                        }
                    });
                }
            } catch (Exception e) {
                logger.error("get Znode Data errer {}", e);
            } finally {
                lock.writeLock().unlock();
            }
        }
        return resource;
    }

    // 清理节点数据相关方法及属性,子类可重写，默认不做操作
    protected Predicate<T> cleanUpOperation() {
        return null;
    }

    protected long waitStopPeriod() {
        return 0L;
    }


    protected void clean(T oldResource) {
        if (oldResource == null) return;
        Predicate<T> operation = cleanUpOperation();
        if (operation == null) return;

        // 清理略费时，开启线程另作处理
        Thread cleanUpThread = new Thread(() -> {
            do {
                long waitStopPeriod = this.waitStopPeriod();
                if (waitStopPeriod > 0) {
                    try {
                        Thread.sleep(waitStopPeriod);
                    } catch (InterruptedException e) {
                        logger.error("clean oldResource {}", e);
                    }

                }
                if (operation.test(oldResource)) {
                    break;
                }

            } while (true);
            logger.info("successfully close old resource:{}", oldResource);
        }, "old [" + oldResource.getClass().getSimpleName() + "] cleanup thread-["
                + oldResource.hashCode() + "]");
        cleanUpThread.setUncaughtExceptionHandler((t, e) -> {
            logger.error("fail to cleanup resource.", e);
        });
        cleanUpThread.start();

    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            Predicate<T> cleanUp;
            if (resource != null && (cleanUp = cleanUpOperation()) != null) {
                cleanUp.test(resource);
            }
        }
    }


}
