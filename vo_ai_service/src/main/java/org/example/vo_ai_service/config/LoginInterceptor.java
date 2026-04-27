package org.example.vo_ai_service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.vo_ai_service.comm.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }
        token = token.substring(7);
        if (!JwtUtil.validate(token)) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期\"}");
            return false;
        }
        // 将userId放入request属性，方便Controller获取
        request.setAttribute("userId", JwtUtil.getUserId(token));
        request.setAttribute("type", JwtUtil.getType(token));
        return true;
    }
}
