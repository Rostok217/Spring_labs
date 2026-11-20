
package com.example.spring2.service;

import com.example.spring2.config.OperationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BeanDemoService {

    @Autowired
    private ApplicationContext context;

    public String checkBeans() {

        OperationInfo singleton1 = context.getBean(
                "singletonOperation", OperationInfo.class);

        OperationInfo singleton2 = context.getBean(
                "singletonOperation", OperationInfo.class);

        OperationInfo prototype1 = context.getBean(
                "prototypeOperation", OperationInfo.class);

        OperationInfo prototype2 = context.getBean(
                "prototypeOperation", OperationInfo.class);

        return "Singleton: " + (singleton1 == singleton2)
                + ", Prototype: " + (prototype1 == prototype2);
    }
}
