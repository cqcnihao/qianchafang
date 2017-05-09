package com.qianchafang.framework.dao;

import com.qianchafang.framework.model.*;
import com.qianchafang.framework.zookeeper.datasource.DataSourceByZNode;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

/**
 * Created by Poan on 2017/5/7.
 * 实体基类的四个基本属性：
 * id
 * `data` 用于存放扩展属性，以json格式存储到data字段
 * status 实体的各种状态
 * createTime 创建时间
 * 由于实体经常需要与其ID一并获取，由对model和Id进行一系列的预处理，以便子类方便操作
 */
public abstract class AbsDataDAO<K extends Number, V extends HasId<K> & HasCreateTime & HasStatus & HasData> extends NamedParameterJdbcDaoSupport {

    protected RowMapper<V> rowMapper;
    protected final String tableName;


    protected AbsDataDAO(String bizName, String tableName, Class<? extends V> clazz) {
        super();
        setDataSource(DataSourceByZNode.of(bizName));
        rowMapper = new BeanPropertyRowMapper(clazz);
        this.tableName = tableName;
    }


}
