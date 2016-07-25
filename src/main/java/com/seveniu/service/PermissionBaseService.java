package com.seveniu.service;

import com.seveniu.data.jdbc.BaseDao;
import com.seveniu.exception.PermissionException;
import com.seveniu.pojo.Pojo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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

    public void del(U user, int id) {
        T t = baseService.getById(id);
        if (!isAuthorizationDelete(user, t)) {
            throw new PermissionException(user, this.getClass(), "del");
        }
        baseService.del(id);
    }

    public int count(U user) {
        if (!isAuthorizationCount(user)) {
            throw new PermissionException(user, this.getClass(), "filterCount");
        }
        return baseService.count();
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
