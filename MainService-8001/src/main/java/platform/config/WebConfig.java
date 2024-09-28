package platform.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import platform.interceptor.IPAddressInterceptor;
import platform.interceptor.SessionTimeoutInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    SessionTimeoutInterceptor sessionTimeoutInterceptor;

    @Resource
    IPAddressInterceptor ipAddressInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionTimeoutInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/mp/internet/wechat/login",
                        "/mp/internet/wechat/getPicCode",
                        "/mp/internet/wechat/verifyPicCode",
                        "/mp/internet/wechat/sendCode"
                );

        registry.addInterceptor(ipAddressInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/mp/internet/wechat/login",
                        "/mp/internet/wechat/getPicCode",
                        "/mp/internet/wechat/verifyPicCode",
                        "/mp/internet/wechat/sendCode"
                );
    }
}
