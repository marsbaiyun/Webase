package com.seveniu.web.api;

import com.alibaba.fastjson.TypeReference;
import com.seveniu.pojo.Pojo;
import com.seveniu.service.BaseService;
import com.seveniu.util.Json;
import com.seveniu.web.ApiResult;
import com.seveniu.web.FilterLimitParams;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by seveniu on 6/2/16.
 * BaseApi
 */
public class BaseApi<T extends Pojo> {

    protected BaseService<T> service;

    public BaseApi(BaseService<T> service) {
        this.service = service;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getById(HttpServletRequest request, @PathVariable("id") int id) {
        try {
            T t = service.getById(id);
            return Json.toJson(t);
        } catch (Exception e) {
            return ApiResult.exception(e).toJson();
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String add(HttpServletRequest request, T field) {
        try {
            int newId = service.insert(field);
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
            service.update(field);
            return ApiResult.success().toJson();
        } catch (DuplicateKeyException e) {
            return ApiResult.get().setCode(ApiResult.EXIST).toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.exception(e).toJson();
        }
    }

    @RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String del(HttpServletRequest request, @PathVariable("id") int id) {
        try {
            service.del(id);
            return ApiResult.success().toJson();
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
            service.del(idList);
            return ApiResult.success().toJson();
        } catch (Exception e) {
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
                service.del(id);
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
        return limit(page, orderColumn, orderType, pagesize).toJson();
    }

    /**
     * list 添加过滤条件
     */
    @RequestMapping(value = "/list-filter", method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
    @ResponseBody
    public String list(HttpServletRequest request) {
        FilterLimitParams params = FilterLimitParams.parseFilterLimit(request);
        return filterLimit(params).toJson();
    }


    protected ApiResult limit(int page, String orderColumn, String orderType, int pagesize) {

        page = page < 1 ? 0 : page;
        int allCount = service.count();
        List<T> bookList = service.limit((page - 1) * pagesize, pagesize, orderColumn, orderType);
        ApiResult result = new ApiResult();
        result.setCode(ApiResult.SUCCESS);
        result.setPage(new ApiResult.Page(page, pagesize, allCount));
        result.setResult(new ApiResult.Result<>(bookList));
        return result;
    }


    protected ApiResult filterLimit(FilterLimitParams params) {

        int page = params.getPage() < 1 ? 0 : params.getPage();
        int start = (page - 1) * params.getPageSize();
        int allCount;
        List<T> list;
        if (params.getLikeField() != null) {
            allCount = service.filterCountLike(params.getFieldArray(), params.getValueArray(), params.getLikeField(), params.getLikeValue());
            list = service.filterLimitLike(start, params.getPageSize(), params.getOrderField(),
                    params.getOrderType(), params.getFieldArray(), params.getValueArray(), params.getLikeField(), params.getLikeValue());
        } else {
            allCount = service.filterCount(params.getFieldArray(), params.getValueArray());
            list = service.filterLimit(start, params.getPageSize(), params.getOrderField(),
                    params.getOrderType(), params.getFieldArray(), params.getValueArray());
        }
        ApiResult result = new ApiResult();
        result.setCode(ApiResult.SUCCESS);
        result.setPage(new ApiResult.Page(page, params.getPageSize(), allCount));
        result.setResult(new ApiResult.Result<>(list));
        return result;
    }

}
