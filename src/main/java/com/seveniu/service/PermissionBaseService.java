package com.seveniu.service;

import com.seveniu.data.jdbc.BaseDao;
import com.seveniu.exception.PermissionException;
import com.seveniu.pojo.Pojo;

import java.util.List;
import java.util.Map;

/**
 * Created by seveniu on 5/27/16.
 * TService
 */
public abstract class PermissionBaseService<U, T extends Pojo> {
    private BaseService<T> baseService;

    public PermissionBaseService(BaseDao<T> dao) {
        this.baseService = new BaseService<>(dao);
    }

    public int insert(U user, T pojo) {
        if (!isAuthorizationInsert(user)) {
            throw new PermissionException(user, this.getClass(), "insert");
        }
        return baseService.insert(pojo);
    }

    public boolean isExist(U user, T pojo) {
        if (!isAuthorizationRead(user, pojo)) {
            throw new PermissionException(user, this.getClass(), "isExist");
        }
        return baseService.isExist(pojo);
    }

    public <K> List<K> getSpecialFieldList(U user, String selectField, String[] filterFields, Object[] filterValues, Class<K> clazz) {
        List<T> list = baseService.all(filterFields, filterValues);
        for (T t : list) {
            if (!isAuthorizationRead(user, t)) {
                throw new PermissionException(user, this.getClass(), "getSpecialFieldList");
            }
        }
        return baseService.getSpecialFieldList(selectField, filterFields, filterValues, clazz);
    }

    public Map<String, Object> getSpecialFieldsById(U user, int id, String... field) {
        T pojo = baseService.getById(id);
        if (pojo != null) {
            if (!isAuthorizationRead(user, pojo)) {
                throw new PermissionException(user, this.getClass(), "getById");
            }
        }
        return baseService.getSpecialFieldsById(id, field);
    }

    public T getById(U user, int id) {
        T pojo = baseService.getById(id);
        if (pojo != null) {
            if (!isAuthorizationRead(user, pojo)) {
                throw new PermissionException(user, this.getClass(), "getById");
            }
        }
        return pojo;
    }

    public List<T> all(U user) {
        List<T> list = baseService.all();
        for (T t : list) {
            if (!isAuthorizationRead(user, t)) {
                throw new PermissionException(user, this.getClass(), "all");
            }
        }
        return list;
    }

    public List<T> all(U user, String[] filterFields, Object[] values) {
        List<T> list = baseService.all(filterFields, values);
        for (T t : list) {
            if (!isAuthorizationRead(user, t)) {
                throw new PermissionException(user, this.getClass(), "all");
            }
        }
        return list;
    }

    public List<T> filterLimit(U user, int start, int size, String fieldName, String orderType, String[] filterColumns, Object[] filterValues) {
        List<T> list = baseService.filterLimit(start, size, fieldName, orderType, filterColumns, filterValues);
        for (T t : list) {
            if (!isAuthorizationRead(user, t)) {
                throw new PermissionException(user, this.getClass(), "filterLimit");
            }
        }
        return list;
    }

    public List<T> filterLimitLike(U user, int start, int size, String fieldName, String orderType, String[] filterFields, Object[] filterValues, String likeField, String likeValue) {
        List<T> list = baseService.filterLimitLike(start, size, fieldName, orderType, filterFields, filterValues, likeField, likeValue);
        for (T t : list) {
            if (!isAuthorizationRead(user, t)) {
                throw new PermissionException(user, this.getClass(), "filterLimitLike");
            }
        }
        return list;
    }

    public List<T> filterOneLimit(U user, int start, int size, String column, String orderType, String filterColumn, Object filterValue) {
        List<T> list = baseService.filterLimit(start, size, column, orderType, new String[]{filterColumn}, new Object[]{filterValue});
        for (T t : list) {
            if (!isAuthorizationRead(user, t)) {
                throw new PermissionException(user, this.getClass(), "filterOneLimit");
            }
        }
        return list;
    }

    public List<T> limit(U user, int start, int size, String column, String orderType) {
        List<T> list = baseService.limit(start, size, column, orderType);
        for (T t : list) {
            if (!isAuthorizationRead(user, t)) {
                throw new PermissionException(user, this.getClass(), "limit");
            }
        }
        return list;
    }

    public void del(U user, List<Integer> ids) {
        for (Integer id : ids) {
            T t = baseService.getById(id);
            if (!isAuthorizationDelete(user, t)) {
                throw new PermissionException(user, this.getClass(), "del");
            }
        }
        baseService.del(ids);
    }

    public void del(U user, int id) {
        T t = baseService.getById(id);
        if (!isAuthorizationDelete(user, t)) {
            throw new PermissionException(user, this.getClass(), "del");
        }
        baseService.del(id);
    }

    public void delByField(U user, String field, Object fieldValue) {
        delByFields(user, new String[]{field}, new Object[]{fieldValue});
    }

    public void delByFields(U user, String[] fields, Object[] fieldValues) {
        List<T> list = baseService.all(fields, fieldValues);
        for (T t : list) {

            if (!isAuthorizationDelete(user, t)) {
                throw new PermissionException(user, this.getClass(), "del");
            }
        }
        baseService.delByFields(fields, fieldValues);
    }

    public int count(U user) {
        if (!isAuthorizationCount(user)) {
            throw new PermissionException(user, this.getClass(), "filterCount");
        }
        return baseService.count();
    }

    public int filterCountLike(U user, String[] filterFields, Object[] filterValues, String likeField, String likeValue) {
        if (!isAuthorizationCount(user)) {
            throw new PermissionException(user, this.getClass(), "filterCount");
        }
        return baseService.filterCountLike(filterFields, filterValues, likeField, likeValue);
    }

    public int filterCount(U user, String[] filterColumns, Object[] filterValues) {
        if (!isAuthorizationCount(user)) {
            throw new PermissionException(user, this.getClass(), "filterCount");
        }
        return baseService.filterCount(filterColumns, filterValues);
    }

    public void update(U user, T pojo) {
        if (!isAuthorizationUpdate(user, pojo)) {
            throw new PermissionException(user, this.getClass(), "update");
        }
        baseService.update(pojo);
    }


    protected BaseService<T> getBaseService() {
        return baseService;
    }

    protected abstract boolean isAuthorizationRead(U accessor, T data);

    protected abstract boolean isAuthorizationUpdate(U accessor, T data);

    protected abstract boolean isAuthorizationInsert(U accessor);

    protected abstract boolean isAuthorizationDelete(U accessor, T data);

    protected abstract boolean isAuthorizationCount(U accessor);

    protected abstract boolean isAuthorizationExcel(U accessor);

}
