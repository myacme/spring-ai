package com.ljx.express;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ljx.express")
public class ExpressAgentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExpressAgentApplication.class, args);
    }
}