package br.com.capitulozero;

import org.springframework.boot.SpringApplication;

public class TestCapituloZeroApplication {

    public static void main(String[] args) {
        SpringApplication.from(CapituloZeroApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
