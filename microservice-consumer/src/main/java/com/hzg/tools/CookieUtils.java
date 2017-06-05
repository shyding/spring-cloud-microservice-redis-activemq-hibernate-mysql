package com.hzg.tools;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class CookieUtils {

    public void addCookie(HttpServletResponse response, String name, String value){
        addCookie(response, name, value, null, true, -1, "/", false);
    }

    /**
     * 添加一个新Cookie
     *
     * @author zifangsky
     * @param response
     *            HttpServletResponse
     * @param cookieName
     *            cookie名称
     * @param cookieValue
     *            cookie值
     * @param domain
     *            cookie所属的子域
     * @param httpOnly
     *            是否将cookie设置成HttpOnly
     * @param maxAge
     *            设置cookie的最大生存期
     * @param path
     *            设置cookie路径
     * @param secure
     *            是否只允许HTTPS访问
     *
     * @return null
     */
    public void addCookie(HttpServletResponse response, String cookieName, String cookieValue, String domain,
                                 boolean httpOnly, int maxAge, String path, boolean secure) {
        if (cookieName != null && !cookieName.equals("")) {
            if (cookieValue == null)
                cookieValue = "";

            Cookie newCookie = new Cookie(cookieName, cookieValue);
            if (domain != null)
                newCookie.setDomain(domain);

            newCookie.setHttpOnly(httpOnly);

            if (maxAge > 0)
                newCookie.setMaxAge(maxAge);

            if (path == null)
                newCookie.setPath("/");
            else
                newCookie.setPath(path);

            newCookie.setSecure(secure);

            response.addCookie(newCookie);
        }
    }

    /**
     * 根据名称获取 cookie
     * @param name
     * @return
     */
    public String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * 根据名称获取 cookie
     * @param name
     * @return
     */
    public Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }

        return null;
    }

    /**
     * 根据cookie名删除指定的cookie
     *
     * @author zifangsky
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param cookieName
     *            待删除cookie名
     */
    public void delCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie cookie = getCookie(request, cookieName);
        if (cookie != null && cookie.getName().equals(cookieName)) {
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setValue(null);

            response.addCookie(cookie);
        }
    }
}