package com.example.GetWellSoon;

import com.example.GetWellSoon.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GetWellSoonApplicationTests {
	@Autowired
	private GeminiService geminiService;
	@Test
	void contextLoads() {
	}

	@Test
	public void sum(){
	assertTrue(3<8);
	}

}
