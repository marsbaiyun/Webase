package com.seveniu.web;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by seveniu on 7/31/16.
 * FilterLimitParams
 */
public class FilterLimitParams {
    private int page;
    private int pageSize;
    private String orderField;
    private String orderType;
    private String likeField;
    private String likeValue;
    private LinkedHashMap<String, Object> otherParams;

    private FilterLimitParams(int page, int pageSize, String orderField, String orderType, LinkedHashMap<String, Object> otherParams, String likeField, String likeValue) {
        this.page = page;
        this.pageSize = pageSize;
        this.orderField = orderField;
        this.orderType = orderType;
        this.otherParams = otherParams;
        if ((likeField == null || likeField.length() == 0) || (likeValue == null || likeValue.length() == 0)) {
            this.likeField = null;
            this.likeValue = null;
        } else {
            this.likeField = likeField;
            this.likeValue = likeValue;
        }
    }


    public static FilterLimitParams parseFilterLimit(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        int page = -1;
        String orderColumn = null;
        String likeField = null;
        String likeValue = null;
        String orderType = null;
        int pageSize = -1;
        LinkedHashMap<String, Object> otherParams = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue()[0];
            if (key.equals("page")) {
                page = Integer.parseInt(value);
            } else if (key.equals("orderColumn")) {
                orderColumn = value.trim();
            } else if (key.equals("orderType")) {
                orderType = value.trim();
            } else if (key.equals("pagesize")) {
                pageSize = Integer.parseInt(value.trim());
            } else {
                if (value == null || value.length() == 0) {
                    continue;
                }
                if (key.startsWith("searchField")) {
                    String[] temp = key.split("_");
                    likeField = temp[1];
                    try {
                        likeValue = "%" + URLDecoder.decode(value, "utf-8").trim() + "%";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    otherParams.put(key, value.trim());
                }
            }
        }
        if (page == -1 || orderColumn == null || orderType == null || pageSize == -1) {
            throw new IllegalArgumentException("page or orderColumn or orderType or pagesize error");
        }
        return new FilterLimitParams(page, pageSize, orderColumn, orderType, otherParams, likeField, likeValue);
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

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
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

    public String getLikeField() {
        return likeField;
    }

    public void setLikeField(String likeField) {
        this.likeField = likeField;
    }

    public String getLikeValue() {
        return likeValue;
    }

    public void setLikeValue(String likeValue) {
        this.likeValue = likeValue;
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
