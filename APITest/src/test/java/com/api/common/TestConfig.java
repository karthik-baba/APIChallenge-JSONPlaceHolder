package com.api.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.util.ResourceUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class TestConfig {
	protected Properties prop;
	public static String baseUri;
	@BeforeSuite
	@Parameters("env")	
	public void setup(@Optional("test") String environment) throws FileNotFoundException, IOException 
	{
		System.out.println("Test Execution Started");
		prop=new Properties();
		prop.load(new FileInputStream(ResourceUtils.getFile("classpath:Config.properties")));
		

		
		
		switch(environment.toLowerCase())
		{
		case "dev":
			baseUri=prop.getProperty("devUrl");
			break;
		case "test":
			baseUri=prop.getProperty("testUrl");
			break;
		case "prod":
			baseUri=prop.getProperty("prodUrl");
			break;
		}
	}

	@AfterSuite
	public void teardown()
	{
		prop.clear();
		System.out.println("Test Execution Completed");
	}

}
