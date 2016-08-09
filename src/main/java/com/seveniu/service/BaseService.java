package com.seveniu.service;

import com.seveniu.data.jdbc.BaseDao;
import com.seveniu.pojo.Pojo;

import java.util.List;
import java.util.Map;

/**
 * Created by seveniu on 5/27/16.
 * TService
 */
public class BaseService<T extends Pojo> {
    protected BaseDao<T> dao;

    protected BaseService(BaseDao<T> dao) {
        this.dao = dao;
    }

    public int insert(T pojo) {
        return dao.insert(pojo);
    }

    public T getBy(String[] fields, Object[] values) {
        return dao.getByOther(fields, values);
    }

    public boolean isExist(String[] fields, Object[] values) {
        return dao.existByColumn(fields, values);
    }

    public boolean isExist(T pojo) {
        return dao.exist(pojo);
    }

    public <K> K getSpecialFieldById(int id, String field, Class<K> clazz) {
        return dao.getSpecialFieldById(id, field, clazz);
    }

    public <K> List<K> getSpecialFieldList(String selectField, String[] filterFields, Object[] filterValues, Class<K> clazz) {
        return dao.getSpecialFieldList(selectField, filterFields, filterValues, clazz);
    }

    public List<Map<String, Object>> getSpecialFields(String[] selectFields, String[] filterFields, Object[] filterValues) {
        if (selectFields == null || selectFields.length == 0) {
            throw new IllegalArgumentException("select fields is null");
        }
        if (filterFields == null || filterFields.length == 0) {
            throw new IllegalArgumentException("select fields is null");
        }
        if (filterValues == null || filterValues.length == 0) {
            throw new IllegalArgumentException("select fields is null");
        }
        if (filterFields.length != filterValues.length) {
            throw new IllegalArgumentException("filter fields length is not equals filter values length");
        }
        return dao.getSpecialFieldMap(selectFields, filterFields, filterValues);
    }

    public Map<String, Object> getSpecialFieldsById(int id, String... fields) {
        if (fields.length == 0) {
            throw new IllegalArgumentException("fields is null");
        }
        return dao.getSpecialFieldsById(id, fields);
    }

    public void delByField(String field, Object fieldValue) {
        dao.delByField(field, fieldValue);
    }

    public void delByFields(String[] fields, Object[] fieldValues) {
        dao.delByFields(fields, fieldValues);
    }

    public T getById(int id) {
        return dao.getById(id);
    }

    public List<T> all() {
        return dao.all();
    }

    public List<T> all(String[] filterFields, Object[] values) {
        return dao.all(filterFields, values);
    }


    public List<T> filterLimit(int start, int size, String fieldName, String orderType, String[] filterFields, Object[] filterValues) {
        return dao.filterLimit(start, size, fieldName, orderType, filterFields, filterValues);
    }

    public List<T> filterLimitLike(int start, int size, String fieldName, String orderType, String[] filterFields, Object[] filterValues, String likeField, String likeValue) {
        return dao.filterLimit(start, size, fieldName, orderType, filterFields, filterValues, likeField, likeValue);
    }

    public List<T> filterOneLimit(int start, int size, String column, String orderType, String filterField, Object filterValue) {
        return dao.filterLimit(start, size, column, orderType, new String[]{filterField}, new Object[]{filterValue});
    }

    public List<T> limit(int start, int size, String field, String orderType) {
        return dao.limit(start, size, field, orderType);
    }

    public void del(List<Integer> ids) {
        dao.del(ids);
    }

    public void del(int id) {
        dao.del(id);
    }

    public int count() {
        return dao.count();
    }

    public int filterCountLike(String[] filterFields, Object[] filterValues, String likeField, String likeValue) {
        return dao.filterCount(filterFields, filterValues,likeField,likeValue);
    }

    public int filterCount(String[] filterFields, Object[] filterValues) {
        return dao.filterCount(filterFields, filterValues);
    }

    public void update(T pojo) {
        dao.update(pojo);
    }

}
