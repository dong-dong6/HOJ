package top.hcode.hoj.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.hcode.hoj.annotation.AnonApi;

import javax.annotation.PostConstruct;

/**
 * @Author: Himit_ZH
 * @Date: 2020/7/19 22:53
 * @Description: Sa-Token 配置类
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    /**
     * 重写 Sa-Token 的注解处理器，增加对 @AnonApi 注解的支持
     */
    @PostConstruct
    public void rewriteSaStrategy() {
        // 重写Sa-Token的注解处理器，增加注解合并功能 
        SaStrategy.instance.isAnnotationPresent = (element, annotationClass) -> {
            // 如果是检查 SaIgnore 注解，同时也检查 AnonApi 注解
            if (annotationClass.getName().equals("cn.dev33.satoken.annotation.SaIgnore")) {
                return AnnotatedElementUtils.isAnnotated(element, annotationClass) 
                    || AnnotatedElementUtils.isAnnotated(element, AnonApi.class);
            }
            return AnnotatedElementUtils.isAnnotated(element, annotationClass);
        };
    }
}
