
package com.poo.miapi;

import io.github.cdimascio.dotenv.Dotenv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

class MiapiApplicationTests {
	static {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USER", dotenv.get("DB_USER"));
		System.setProperty("DB_PASS", dotenv.get("DB_PASS"));
	}

	@Test
	void contextLoads() {
	}

}
