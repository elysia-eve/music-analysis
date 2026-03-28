package com.music.emotion;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.music.emotion.mapper")
@SpringBootApplication
@EnableScheduling
public class EmotionAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmotionAnalysisApplication.class, args);
    }

}
