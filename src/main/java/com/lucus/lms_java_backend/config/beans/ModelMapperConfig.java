/*
 * @Author : Linn Myat Maung
 * @Date   : 4/8/2025
 * @Time   : 5:22 PM
 */

package com.lucus.lms_java_backend.config.beans;

import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }
}