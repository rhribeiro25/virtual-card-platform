package br.com.rhribeiro25.virtual_card_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VirtualCardPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualCardPlatformApplication.class, args);
    }

}
