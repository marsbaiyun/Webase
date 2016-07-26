package com.seveniu.web.api;

import com.alibaba.fastjson.TypeReference;
import com.seveniu.pojo.Pojo;
import com.seveniu.service.PermissionBaseService;
import com.seveniu.util.Json;
import com.seveniu.web.ApiResult;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by seveniu on 6/2/16.
 * BaseApi
 */
public abstract class PermissionBaseApi<U, T extends Pojo> {

    protected PermissionBaseService<U, T> service;

    public PermissionBaseApi(PermissionBaseService<U, T> service) {
        this.service = service;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getById(HttpServletRequest request, @PathVariable("id") int id) {
        try {
            T t = service.getById(getAccessor(request), id);
            return Json.toJson(t);
        } catch (Exception e) {
            return ApiResult.exception(e).toJson();
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String list(HttpServletRequest request, T field) {
        try {
            int newId = service.insert(getAccessor(request), field);
            return ApiResult.success().setMessage(newId).toJson();
        } catch (DuplicateKeyException e) {
            return ApiResult.get().setCode(ApiResult.EXIST).toJson();
        } catch (Exception e) {
            return ApiResult.exception(e).toJson();
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String edit(HttpServletRequest request, T field) {
        try {
            if (field.getId() == null) {
                return ApiResult.get().setCode(ApiResult.PARAM_ERROR).setMessage("id is null").toJson();
            }
            service.update(getAccessor(request), field);
            return ApiResult.success().toJson();
        } catch (DuplicateKeyException e) {
            return ApiResult.get().setCode(ApiResult.EXIST).toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.exception(e).toJson();
        }
    }

    @RequestMapping(value = "/del-list", method = RequestMethod.DELETE, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String delList(HttpServletRequest request, String ids) {
        try {
            List<Integer> idList = Json.parse(ids, new TypeReference<List<Integer>>() {
            });
            service.del(getAccessor(request), idList);
            return ApiResult.success().toJson();
        } catch (Exception e) {
            return ApiResult.exception(e).toJson();
        }
    }

    @RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String del(HttpServletRequest request, @PathVariable("id") int id) {
        try {
            service.del(getAccessor(request), id);
            return ApiResult.success().toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.exception(e).toJson();
        }
    }

    @RequestMapping(value = "/del", method = RequestMethod.DELETE, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String del(HttpServletRequest request, String ids) {
        String[] idStrArray = ids.split(",");
        int[] idsArray = new int[idStrArray.length];
        for (int i = 0; i < idsArray.length; i++) {
            try {
                idsArray[i] = Integer.parseInt(idStrArray[i]);
            } catch (NumberFormatException e) {
                return ApiResult.get().setCode(ApiResult.PARAM_ERROR).toJson();
            }

        }
        try {
            for (int id : idsArray) {
                service.del(getAccessor(request), id);
            }
            return ApiResult.success().toJson();
        } catch (Exception e) {
            return ApiResult.exception(e).toJson();
        }
    }

    /**
     * 全部列别,没有过滤条件
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String list(int page, String orderColumn, String orderType, int pagesize, HttpServletRequest request) {
        return limit(getAccessor(request), page, orderColumn, orderType, pagesize).toJson();
    }

    /**
     * list 添加过滤条件
     */
    @RequestMapping(value = "/list-filter", method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String list(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        int page = -1;
        String orderColumn = null;
        String orderType = null;
        int pageSize = -1;

        int filterSize = params.size() - 4;
        String[] filterColumns = new String[filterSize];
        Object[] filterValues = new Object[filterSize];
        int i = 0;
        for (Map.Entry<String, String[]> entry : params.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue()[0];
            if (key.equals("page")) {
                page = Integer.parseInt(value);
            } else if (key.equals("orderColumn")) {
                orderColumn = value;
            } else if (key.equals("orderType")) {
                orderType = value;
            } else if (key.equals("pagesize")) {
                pageSize = Integer.parseInt(value);
            } else {
                if (value == null || value.length() == 0) {
                    continue;
                }
                int index = i++;
                filterColumns[index] = key;
                filterValues[index] = value;
            }
        }
        if (page == -1 || orderColumn == null || orderType == null || pageSize == -1) {
            return ApiResult.exception(new IllegalArgumentException("page or orderColumn or orderType or pagesize error")).toJson();
        }
        return filterLimit(getAccessor(request), page, orderColumn, orderType, pageSize, filterColumns, filterValues).toJson();
    }

    protected ApiResult limit(U user, int page, String orderColumn, String orderType, int pagesize) {

        int allCount = service.count(user);
        List<T> list = service.limit(user, (page - 1) * pagesize, pagesize, orderColumn, orderType);
        ApiResult result = new ApiResult();
        result.setCode(ApiResult.SUCCESS);
        result.setPage(new ApiResult.Page(page, pagesize, allCount));
        result.setResult(new ApiResult.Result<>(list));
        return result;
    }

    protected ApiResult filterLimit(U user, int page, String orderColumn, String orderType, int pagesize, String[] fieldArray, Object[] valueArray) {

        int allCount = service.filterCount(user, fieldArray, valueArray);
        List<T> list = service.filterLimit(user, (page - 1) * pagesize, pagesize, orderColumn, orderType, fieldArray, valueArray);
        ApiResult result = new ApiResult();
        result.setCode(ApiResult.SUCCESS);
        result.setPage(new ApiResult.Page(page, pagesize, allCount));
        result.setResult(new ApiResult.Result<>(list));
        return result;
    }


    protected abstract U getAccessor(HttpServletRequest request);
}
