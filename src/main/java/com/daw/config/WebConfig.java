package com.daw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar el manejo de recursos estáticos
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0); // Sin caché durante desarrollo
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Configurar la página de inicio
        registry.addViewController("").setViewName("index.html");
        registry.addViewController("/").setViewName("index.html");
    }
}
