package br.com.rhribeiro25.virtual_card_platform;

import org.springframework.boot.SpringApplication;

public class TestVirtualCardPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(VirtualCardPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
