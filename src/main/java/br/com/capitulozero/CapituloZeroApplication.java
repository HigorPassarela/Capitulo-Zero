package br.com.capitulozero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@ConfigurationPropertiesScan
@Modulithic(systemName = "Capítulo Zero", sharedModules = "shared")
public class CapituloZeroApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapituloZeroApplication.class, args);
    }
}
