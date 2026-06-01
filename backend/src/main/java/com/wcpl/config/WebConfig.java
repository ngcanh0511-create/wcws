package com.wcpl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.avatar-dir}")
    private String avatarDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // User uploads từ filesystem, default avatars từ classpath (fallback)
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations(
                        "file:" + avatarDir + "/",
                        "classpath:/static/avatars/"
                );
    }
}
