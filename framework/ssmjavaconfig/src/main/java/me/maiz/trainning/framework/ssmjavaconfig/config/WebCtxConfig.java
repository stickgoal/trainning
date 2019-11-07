package me.maiz.trainning.framework.ssmjavaconfig.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@ComponentScan("me.maiz.trainning.framework.ssmjavaconfig.web")
@EnableWebMvc
public class WebCtxConfig implements WebMvcConfigurer {
    /**
     * 配置视图解析器
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/page", ".jsp");
        registry.enableContentNegotiation();
    }

    /**
     * 静态资源处理
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/style/**").addResourceLocations("/WEB-INF/css/");
    }

    /**
     * 文件上传的部分
     * @return
     */
    @Bean
    public MultipartResolver multipartResolver(){
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(10485760);
        multipartResolver.setMaxUploadSizePerFile(2097152);
        return multipartResolver;
    }
}
