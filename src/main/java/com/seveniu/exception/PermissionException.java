package com.seveniu.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by seveniu on 7/8/16.
 * 权限不足时,抛出该异常
 * PermissionException
 */
public class PermissionException extends RuntimeException {

    private static Logger logger = LoggerFactory.getLogger(PermissionException.class);

    public PermissionException(Object uid, Class clazz, String method) {
        super("user : " + uid + " has no permission to execute : " + clazz + "@" + method + ".");
        logger(uid, clazz, method, "");
    }

    public PermissionException(int uid, Class clazz, String method, String message) {
        super("user : " + uid + " has no permission to execute : " + clazz + "@" + method + " . message : " + message);
        logger(uid, clazz, method, message);
    }

    private void logger(Object uid, Class clazz, String method, String message) {
        logger.warn("user : {} has no permission to execute : {}@{} . message : {}", uid, clazz, method, message);
    }
}
