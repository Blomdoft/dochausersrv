package com.hauser.dochausersrv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Value("${archive.path}")
    private String archivePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/archive/**").addResourceLocations("file://" + archivePath);
    }

}
