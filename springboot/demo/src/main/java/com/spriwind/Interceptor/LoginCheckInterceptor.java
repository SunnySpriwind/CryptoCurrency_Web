package com.spriwind.Interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.spriwind.Entity.Result;
import com.spriwind.Utils.JWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Sunny Spriwind
 * @since
 */

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // To get url
        String url = request.getRequestURI().toString();
        log.info("url:{}", url);

        // If url includes "/login" or ""/register
        if (url.contains("login") || url.contains("/register")) {
            log.info("login or register");
            return true;
        }

        // To get JWT
        String jwt = request.getHeader("token");

        // If JWT is valid
        if (!StringUtils.hasLength(jwt)) {
            log.info("token is null");
            Result error = Result.error("Not Login");
            String notLogin = JSONObject.toJSONString(error);
            response.getWriter().write(notLogin);
            return false;
        }

        //To parse JWT
        try{
            JWT.parseJWT(jwt);
        } catch (Exception e){
            e.printStackTrace();
            log.info("parse jwt error");
            Result error = Result.error("Not Login");
            String notLogin = JSONObject.toJSONString(error);
            response.getWriter().write(notLogin);
            return false;
        }

        log.info("successfully");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
