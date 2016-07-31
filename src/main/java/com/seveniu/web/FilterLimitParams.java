package com.seveniu.web;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by seveniu on 7/31/16.
 */
public class FilterLimitParams {
    private int page;
    private int pageSize;
    private String orderColumn;
    private String orderType;
    private List<String> fieldList;
    private List<Object> valueList;

    public FilterLimitParams(int page, int pageSize, String orderColumn, String orderType, List<String> fieldList, List<Object> valueList) {
        this.page = page;
        this.pageSize = pageSize;
        this.orderColumn = orderColumn;
        this.orderType = orderType;
        this.fieldList = fieldList;
        this.valueList = valueList;
    }

    public static FilterLimitParams parseFilterLimit(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        int page = -1;
        String orderColumn = null;
        String orderType = null;
        int pageSize = -1;
        List<String> filterColumns = new ArrayList<>();
        List<Object> filterValues = new ArrayList<>();
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
                filterColumns.add(key);
                filterValues.add(value);
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

    public List<String> getFieldList() {
        return fieldList;
    }

    public String[] getFieldArray() {
        String[] array = new String[fieldList.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = fieldList.get(i);
        }
        return array;
    }

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }

    public List<Object> getValueList() {
        return valueList;
    }

    public Object[] getValueArray() {
        Object[] array = new Object[valueList.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = fieldList.get(i);
        }
        return array;
    }

    public void setValueList(List<Object> valueList) {
        this.valueList = valueList;
    }
}
