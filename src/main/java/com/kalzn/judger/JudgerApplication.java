package com.kalzn.judger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class JudgerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JudgerApplication.class, args);
    }

}
