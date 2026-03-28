package com.music.emotion.Interceptor;

import com.music.emotion.util.JwtUtil;
import com.music.emotion.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("拦截器执行，请求路径：{}", request.getRequestURI());

        // 从请求头中获取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Map<String, Object> claims = JwtUtil.parseToken(token);
                Object userIdObj = claims.get("userId");
                if (userIdObj != null) {
                    Long userId = Long.valueOf(userIdObj.toString());
                    UserContext.setCurrentUserId(userId);
                }
                return true;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
        // 未携带 token 时，视为未登录用户，继续执行（业务层可判断 userId 是否为 null）
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
