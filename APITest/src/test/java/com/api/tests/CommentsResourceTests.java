package com.api.tests;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.api.common.TestConfig;
import com.api.pojo.CommentPojo;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class CommentsResourceTests extends TestConfig {
	public static final String RESOURCE_UNDERTEST="comments";
	private RequestSpecification spec;

	@BeforeTest
	public void init()
	{
		this.spec = new RequestSpecBuilder()
				.setBaseUri(TestConfig.baseUri)
				.build();
	}
	
	/**
	 * Testing if the service returns 200
	 * Resource - /comments
	 * Method - GET
	 */
	@Test
	public void getAllComments()
	{
		given().
			spec(this.spec).
		when().
			get(RESOURCE_UNDERTEST).
		then().
			contentType(ContentType.JSON).			
			statusCode(200);
	}
	
	/**
	 * Testing if the resource comments returns 500 records
	 * Method - GET
	 */
	@Test(dependsOnMethods={"getAllComments"})
	public void checkCommentsCount()
	{
		
		CommentPojo[] commentObjs =
				given().
					spec(this.spec).
				when().
					get(RESOURCE_UNDERTEST).as(CommentPojo[].class);
		Assert.assertEquals(commentObjs.length, 500);		
	}
	
	/**
	 * Testing if the resource with paramters
	 * postid,emailId
	 * Method - GET
	 */
	@Test(dependsOnMethods={"getAllComments"})
	public void checkCommentsWithParameters()
	{
		
		CommentPojo[] commentObjs =
				given().
					spec(this.spec).
					param("postId", 1).
					param("email","Lew@alysha.tv").
				when().
					get(RESOURCE_UNDERTEST).as(CommentPojo[].class);
		Assert.assertEquals(commentObjs.length, 1);	
		Assert.assertEquals(commentObjs[0].getEmail(), "Lew@alysha.tv");	
		Assert.assertEquals(commentObjs[0].getPostId(), 1);
	}
	
	/**
	 * Testing if the resource with invalid paramters
	 * postid,emailId
	 * Method - GET
	 * List should be empty
	 */
	@Test(dependsOnMethods={"getAllComments"})
	public void checkCommentsWithInvalidParameters()
	{
		
		CommentPojo[] commentObjs =
				given().
					spec(this.spec).
					param("postId", -1).
					param("email","Lew@alysha.tv").
				when().
					get(RESOURCE_UNDERTEST).as(CommentPojo[].class);
		
		Assert.assertEquals(commentObjs.length, 0);	
		
	}
	
	
}
