package com.wechat.routine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com.wechat.routine.mapper"})
@SpringBootApplication
public class RoutineApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoutineApplication.class, args);
    }
}
