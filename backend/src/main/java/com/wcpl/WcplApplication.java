package com.wcpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WcplApplication {

    public static void main(String[] args) {
        SpringApplication.run(WcplApplication.class, args);
    }
}
