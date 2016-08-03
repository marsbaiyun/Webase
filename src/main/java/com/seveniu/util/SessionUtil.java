package com.seveniu.util;


import javax.servlet.http.HttpSession;

/**
 * Remark:
 * <p/>
 * Date: 12/9/13 15:15
 */
public class SessionUtil {

    public static final String SESSION_LOGIN_VALID = "SESSION_LOGIN_VALID";
    public static final String SESSION_USER_NAME = "SESSION_USER_NAME";
    public static final String SESSION_USER_ID = "SESSION_USER_ID";
    public static final String SESSION_USER = "SESSION_USER";
    public static final String SESSION_USER_ROLE = "SESSION_USER_ROLE";
    public static final String SESSION_USER_GROUP = "SESSION_USER_GROUP";
    public static final String SESSION_IS_ADMIN = "SESSION_IS_ADMIN";
    public static final String SESSION_IS_OUTER = "SESSION_IS_OUTER";

    public static boolean isValid(HttpSession session) {
        try {
            return ((Boolean) session.getAttribute(SESSION_LOGIN_VALID));
        } catch (Exception e) {
            return false;
        }
    }

    public static void login(HttpSession session, Object user) {
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_LOGIN_VALID, true);
    }

    public static void login(HttpSession session, int userId) {
        session.setAttribute(SESSION_USER_ID, userId);
        session.setAttribute(SESSION_LOGIN_VALID, true);
    }

    public static void login(HttpSession session, String username, int userId, boolean isAdmin, int userRole, Object user) {
        login(session, username, userId, isAdmin, userRole);
        session.setAttribute(SESSION_USER, user);
    }

    public static void login(HttpSession session, String username, int userId, boolean isAdmin, int userRole) {
        session.setAttribute(SESSION_USER_NAME, username);
        session.setAttribute(SESSION_USER_ID, userId);
        session.setAttribute(SESSION_IS_ADMIN, isAdmin);
        session.setAttribute(SESSION_USER_ROLE, userRole);
        session.setAttribute(SESSION_LOGIN_VALID, true);
    }

    public static Object get(HttpSession session, String key) {
        return session.getAttribute(key);
    }

    public static void destroy(HttpSession session) {
        session.invalidate();
    }

    public static void dump(HttpSession session) {
        System.out.println(session.getAttribute(SESSION_LOGIN_VALID));
        System.out.println(session.getAttribute(SESSION_USER_NAME));
        System.out.println(session.getAttribute(SESSION_USER_ID));
        System.out.println(session.getAttribute(SESSION_USER_GROUP));
        System.out.println(session.getAttribute(SESSION_IS_ADMIN));
        System.out.println(session.getAttribute(SESSION_IS_OUTER));
    }

    public static String username(HttpSession session) {
        return (String) session.getAttribute(SESSION_USER_NAME);
    }

    public static int userId(HttpSession session) {
        return (int) session.getAttribute(SESSION_USER_ID);
    }

    public static Object user(HttpSession session) {
        return session.getAttribute(SESSION_USER);
    }

    public static boolean isAdmin(HttpSession session) {
        try {
            return (Boolean) session.getAttribute(SESSION_IS_ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasGroup(HttpSession session) {
        try {
            return (Boolean) session.getAttribute(SESSION_IS_ADMIN);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isOuter(HttpSession session) {
        try {
            return (Boolean) session.getAttribute(SESSION_IS_OUTER);
        } catch (Exception e) {
            return false;
        }
    }

}
