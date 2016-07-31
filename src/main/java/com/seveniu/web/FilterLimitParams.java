package com.seveniu.web;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by seveniu on 7/31/16.
 */
public class FilterLimitParams {
    int page;
    int pageSize;
    String orderColumn;
    String orderType;
    String[] fieldArray;
    Object[] valueArray;

    public FilterLimitParams(int page, int pageSize, String orderColumn, String orderType, String[] fieldArray, Object[] valueArray) {
        this.page = page;
        this.pageSize = pageSize;
        this.orderColumn = orderColumn;
        this.orderType = orderType;
        this.fieldArray = fieldArray;
        this.valueArray = valueArray;
    }


    public static FilterLimitParams parseFilterLimit(HttpServletRequest request) {
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
            throw new IllegalArgumentException("page or orderColumn or orderType or pagesize error");
        }
        return new FilterLimitParams(page, pageSize, orderColumn, orderType, filterColumns, filterValues);
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

    public String[] getFieldArray() {
        return fieldArray;
    }

    public void setFieldArray(String[] fieldArray) {
        this.fieldArray = fieldArray;
    }

    public Object[] getValueArray() {
        return valueArray;
    }

    public void setValueArray(Object[] valueArray) {
        this.valueArray = valueArray;
    }
}
