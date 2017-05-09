package com.qianchafang.framework.dao;

import com.google.common.collect.Iterables;
import com.qianchafang.framework.model.*;
import com.qianchafang.framework.util.ObjectMapperUtils;
import com.qianchafang.framework.zookeeper.datasource.DataSourceByZNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.*;
import java.util.function.Consumer;

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

    protected static final int MAX_RETREIVE_SIZE = 100;

    protected static final int MIN_INITIAL_CAPACITY = 16;

    protected final String tableName;

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    protected AbsDataDAO(String bizName, String tableName, Class<? extends V> clazz) {
        super();
        setDataSource(DataSourceByZNode.of(bizName));
        rowMapper = new BeanPropertyRowMapper(clazz);
        this.tableName = tableName;
    }

    public Number insert(String data, long createTime) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(
                "insert into " + tableName + "(data, create_time) values(:d,:ct)",
                new MapSqlParameterSource("d", data).addValue("ct", createTime), keyHolder);
        return keyHolder.getKey();
    }

    public Number insert(String data) {
        return insert(data, System.currentTimeMillis());
    }

    public Number insertEx(Map<String, Object> data, long createTime) {
        if (data != null && data.size() == 0) {
            data = new TreeMap<>(data);
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(
                "insert into " + tableName + "(data, create_time) values(:d,:ct)",
                new MapSqlParameterSource("d", ObjectMapperUtils.toJSON(data)).addValue("ct",
                        createTime),
                keyHolder);
        return keyHolder.getKey();
    }

    public int updateDataWithoutComparingOldValue(K id, String newValue) {
        return getNamedParameterJdbcTemplate().update(
                "update " + tableName + " set data=:newData where id=:id",
                new MapSqlParameterSource("id", id).addValue("newData", newValue));
    }


    public int updateData(K id, String newValue, String oldValue) {
        return getNamedParameterJdbcTemplate().update(
                "update " + tableName + " set data=:newData where id=:id and data=:oldData",
                new MapSqlParameterSource("id", id).addValue("newData", newValue)
                        .addValue("oldData", oldValue));
    }

    public int updateDataWithoutComparingOldValue(K id, Consumer<Map<String, Object>> consumer,
                                                  HasData data) {
        Map<String, Object> newValues = new HashMap<>(data.resolvedData());
        consumer.accept(newValues);
        String newValue = ObjectMapperUtils.toJSON(newValues);
        int update = updateDataWithoutComparingOldValue(id, newValue);
        if (update > 0) {
            data.updateData(newValue);
        }
        return update;
    }

    public int updateData(K id, Consumer<Map<String, Object>> consumer, HasData data) {
        Map<String, Object> newValues = new HashMap<>(data.resolvedData());
        consumer.accept(newValues);

        String newValue = ObjectMapperUtils.toJSON(newValues);
        int update = updateData(id, newValue, data.getData());
        if (update > 0) {
            data.updateData(newValue);
        }

        return update;
    }

    public int updateDataEx(K id, Consumer<Map<String, Object>> consumer, HasData data) {
        Map<String, Object> newValues = new TreeMap<>(data.resolvedData());
        consumer.accept(newValues);

        String newValue = ObjectMapperUtils.toJSON(newValues);

        int updateData = updateData(id, newValue,
                ObjectMapperUtils.toJSON(new TreeMap<>(data.resolvedData())));
        if (updateData > 0) {
            data.updateData(newValue);
        }
        return updateData;
    }

    public int setStatus(K id, EntityStatus.EStatus status) {
        return getNamedParameterJdbcTemplate().update(
                "update " + tableName + " set status=:status where id=:id",
                new MapSqlParameterSource("id", id).addValue("status", status.getValue()));
    }

    public V getById(K id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return getByIds(Collections.singleton(id)).get(id);
    }

    public Map<K, V> getByIds(Collection<K> ids) {
        if (ids == null || ids.size() == 0) {
            return Collections.emptyMap();
        }
        Map<K, V> result = new HashMap<>(Math.max(MIN_INITIAL_CAPACITY, ids.size()));
        Iterable<List<K>> partitionedIds = Iterables.partition(ids, MAX_RETREIVE_SIZE);
        for (List<K> thisIds : partitionedIds) {
            getNamedParameterJdbcTemplate().query(
                    "select * from " + tableName + " where id in (:ids)",
                    new MapSqlParameterSource("ids", thisIds), rs -> {

                        V model = rowMapper.mapRow(rs, 0);
                        if (model != null) {
                            result.put(model.getId(), model);
                        } else {
                            if (tableName != "chat_msg") {//chatmsg中有一个类型（互相点赞成好友）没有model与之对应，另外ChatMsgDAO中自带了一个log
                                logger.warn("mapping empty model for {}", tableName);
                            }
                        }
                    });
        }
        return result;
    }

}
