package com.qianchafang.framework.zookeeper;

import java.io.Closeable;

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

    /**
     * 由子类实现，将当前节点中存贮json数据传入，返回其泛型类型
     *
     * @param zkValue 节点中存贮的数据(统一以json形式保存)
     * @return 子类指定的泛型类型
     */
    protected abstract T initObject(String zkValue);


}
