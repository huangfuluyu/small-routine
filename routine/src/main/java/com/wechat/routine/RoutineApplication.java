package com.wechat.routine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@MapperScan(basePackages = {"com.wechat.routine.mapper"})
@SpringBootApplication
@EnableSwagger2
public class RoutineApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoutineApplication.class, args);
    }
}
