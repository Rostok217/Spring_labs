
package com.example.spring2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean("singletonOperation")
    @Scope("singleton")
    public OperationInfo singletonOperation() {
        return new OperationInfo();
    }

    @Bean("prototypeOperation")
    @Scope("prototype")
    public OperationInfo prototypeOperation() {
        return new OperationInfo();
    }
}
