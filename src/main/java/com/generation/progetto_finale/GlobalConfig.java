package com.generation.progetto_finale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalConfig implements WebMvcConfigurer
{
    @Value("${urlprefix}")
    private String prefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) 
    {
        configurer.addPathPrefix(prefix, c -> true);
    }
}
