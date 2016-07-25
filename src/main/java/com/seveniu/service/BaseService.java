package com.seveniu.service;

import com.seveniu.data.jdbc.BaseDao;
import com.seveniu.pojo.Pojo;

import java.util.List;

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

    public List<T> filterOneLimit(int start, int size, String column, String orderType, String filterField, Object filterValue) {
        return dao.filterLimit(start, size, column, orderType, new String[]{filterField}, new Object[]{filterValue});
    }

    public List<T> limit(int start, int size, String field, String orderType) {
        return dao.limit(start, size, field, orderType);
    }

    public void del(int id) {
        dao.del(id);
    }

    public int count() {
        return dao.count();
    }

    public int filterCount(String[] filterFields, Object[] filterValues) {
        return dao.filterCount(filterFields, filterValues);
    }

    public void update(T pojo) {
        dao.update(pojo);
    }

}
