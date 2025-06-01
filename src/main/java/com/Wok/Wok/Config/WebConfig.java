package com.Wok.Wok.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String mediaPath = "file:///" + uploadDir.replace("\\", "/") + "/";
        logger.info("Configuring media resource handler:");
        logger.info("uploadDir property: {}", uploadDir);
        logger.info("Resolved mediaPath: {}", mediaPath);

        registry.addResourceHandler("/media/**")
                .addResourceLocations(mediaPath);
    }
}
