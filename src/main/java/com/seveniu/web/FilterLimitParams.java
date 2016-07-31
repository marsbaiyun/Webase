package com.seveniu.web;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by seveniu on 7/31/16.
 * FilterLimitParams
 */
public class FilterLimitParams {
    private int page;
    private int pageSize;
    private String orderColumn;
    private String orderType;
    private LinkedHashMap<String, Object> otherParams;

    private FilterLimitParams(int page, int pageSize, String orderColumn, String orderType, LinkedHashMap<String, Object> otherParams) {
        this.page = page;
        this.pageSize = pageSize;
        this.orderColumn = orderColumn;
        this.orderType = orderType;
        this.otherParams = otherParams;
    }


    public static FilterLimitParams parseFilterLimit(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        int page = -1;
        String orderColumn = null;
        String orderType = null;
        int pageSize = -1;
        LinkedHashMap<String, Object> otherParams = new LinkedHashMap<>();
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
                otherParams.put(key, value);
            }
        }
        if (page == -1 || orderColumn == null || orderType == null || pageSize == -1) {
            throw new IllegalArgumentException("page or orderColumn or orderType or pagesize error");
        }
        return new FilterLimitParams(page, pageSize, orderColumn, orderType, otherParams);
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public LinkedHashMap<String, Object> getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(LinkedHashMap<String, Object> otherParams) {
        this.otherParams = otherParams;
    }

    public String[] getFieldArray() {
        String[] array = new String[otherParams.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
            array[i] = entry.getKey();
            i++;
        }
        return array;
    }


    public Object[] getValueArray() {
        Object[] array = new Object[otherParams.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
            array[i] = entry.getValue();
            i++;
        }
        return array;
    }


}
