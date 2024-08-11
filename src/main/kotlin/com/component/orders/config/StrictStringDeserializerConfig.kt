package com.component.orders.config

import com.component.orders.models.StrictStringDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class StringStringDeserializerConfig {
    @Bean
    open fun strictStringDeserializerModule(): SimpleModule {
        val module = SimpleModule()
        module.addDeserializer(String::class.java, StrictStringDeserializer())
        return module
    }
}