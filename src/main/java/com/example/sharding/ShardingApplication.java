package com.example.sharding;

import com.example.sharding.service.DemoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ShardingApplication {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext applicationContext = SpringApplication.run(ShardingApplication.class, args)) {
            applicationContext.getBean(DemoService.class).update();
        }
    }
}
