package com.wcy;

import com.wcy.service.ScenarioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ScenarioClientApplication implements CommandLineRunner {

	@Autowired
	ScenarioClient client;

	public static void main(String[] args) {
		new SpringApplicationBuilder(ScenarioClientApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Override
	public void run(String... args) throws Exception {

		client.initialize("localhost",8980);

		client.cheekIn();

//		client.chunker();

//        client.getCompanies("companyA");

//		client.batchWriteCompany();

//		client.translate();
		client.blockingUtilShutdown();
	}
}
