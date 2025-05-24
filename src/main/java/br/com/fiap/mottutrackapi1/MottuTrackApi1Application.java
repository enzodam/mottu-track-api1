package br.com.fiap.mottutrackapi1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MottuTrackApi1Application {
    public static void main(String[] args) {SpringApplication.run(MottuTrackApi1Application.class, args);}
}