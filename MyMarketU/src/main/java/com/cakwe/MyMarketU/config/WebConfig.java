package com.cakwe.MyMarketU.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/")
                .setCachePeriod(0);
                
        // Tambahkan handler khusus untuk folder profile
        registry.addResourceHandler("/img/profile/**")
                .addResourceLocations("file:./src/main/resources/static/img/profile/")
                .setCachePeriod(0);
    }
}