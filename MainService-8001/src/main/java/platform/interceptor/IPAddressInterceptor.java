package platform.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import platform.util.RedisUtil;

@Component
public class IPAddressInterceptor implements HandlerInterceptor {

    @Resource
    RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //It is prohibited for the same account to log into the system from multiple IP addresses
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object uidObj = session.getAttribute("uid");
            if (uidObj == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            String uid = uidObj.toString();
            String savedToken = redisUtil.get(uid);
            String token = request.getHeader("token"); //Token content is IP address
            if (token == null || savedToken == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            if (!token.equals(savedToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
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
