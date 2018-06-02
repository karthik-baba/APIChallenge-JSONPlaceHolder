
package com.api.tests;
import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.api.common.TestConfig;
import com.api.pojo.PostPojo;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class PostsResourceTests {
	public static final String RESOURCE_UNDERTEST="posts";
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
	 * Resource - /posts
	 * Method - GET
	 */
	@Test
	public 	void getAllPosts()
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
	 * Testing if /post resource returns 100 records
	 * Method - GET
	 */
	
	@Test(dependsOnMethods={"getAllPosts"})
	public void checkGetAllPostsCount()
	{
		PostPojo[] postObjs =
				given().
					spec(this.spec).
				when().
					get(RESOURCE_UNDERTEST).as(PostPojo[].class);
		Assert.assertEquals(postObjs.length, 100);
	}
	
	/**
	 * Test /post/1 with valid id
	 * Method - GET
	 */
	@Test(dependsOnMethods={"getAllPosts"})
	public void checkGetValidPost()
	{
		PostPojo postObj =given().spec(this.spec).when().get(RESOURCE_UNDERTEST+"/1").as(PostPojo.class);
		
		Assert.assertEquals(postObj.getId(), 1);
		Assert.assertEquals(postObj.getUserId(), 1);
		Assert.assertEquals(postObj.getTitle(), "sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
		Assert.assertEquals(postObj.getBody(),"quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto");
	}
	
	/**
	 * Test /posts/-1 - Invalid ID
	 * should return 404 -	Not Founds
	 * Method - GET
	 */
	@Test(dependsOnMethods={"getAllPosts"})
	public void checkGetInvalidPost()
	{		
		given().
			spec(this.spec).
		when().
			get(RESOURCE_UNDERTEST+"/-1").
		then().
			statusCode(404);			
	}
	
		
	/**
	 * Test /posts with parameters
	 * Method - GET
	 */
	@Test(dependsOnMethods={"getAllPosts"})
	public void checkPostsWithQueryParameters()
	{		
		PostPojo[] postObjs=
		
		given().
			spec(this.spec).
			param("userId",5).
		when().
			get(RESOURCE_UNDERTEST).as(PostPojo[].class);
		
		Assert.assertEquals(postObjs.length, 10);
		
		//Verifying if all the records contain user id = 5
		List<PostPojo> listofPosts=Arrays.asList(postObjs);
		Assert.assertEquals(listofPosts.stream().filter(post->post.getUserId()==5).collect(Collectors.toList()).size(), 10);
			
					
	}
	
	/**
	 * Test /posts with multiple parameters - userid, id
	 * Method - GET
	 */
	@Test(dependsOnMethods={"getAllPosts"})
	public void checkPostsWithMultipleQueryParameters()
	{		
		PostPojo[] postObjs=
		
		given().
			spec(this.spec).
			param("userId",5).
			param("id",45).
		when().
			get(RESOURCE_UNDERTEST).as(PostPojo[].class);
		
		Assert.assertEquals(postObjs.length, 1);
		Assert.assertEquals(postObjs[0].getUserId(), 5);
		Assert.assertEquals(postObjs[0].getId(), 45);
		Assert.assertEquals(postObjs[0].getTitle(), "ut numquam possimus omnis eius suscipit laudantium iure");
		Assert.assertEquals(postObjs[0].getBody(), "est natus reiciendis nihil possimus aut provident\nex et dolor\nrepellat pariatur est\nnobis rerum repellendus dolorem autem");
	}
	
	
	
	/**
	 * Method - POST
	 * Check if 200 is returned 
	 */
	@Test
	public void checkCreatePost()
	{
		PostPojo postObj= new PostPojo();
		postObj.setTitle("Test Title");
		postObj.setBody("Test Body");
		postObj.setUserId(1);
		
		given().
			spec(this.spec).
			body(postObj).
		when().
			request().
			post(RESOURCE_UNDERTEST).
		then().
			statusCode(201);	
		
	}
	
	/**
	 *  Check if the created record is present
	 *  Methods - POST, GET  
	 *  This test will fail due to service specification
	 *  Note: the resource will not be really created on the server but it will be faked as if.
	 */
	@Test(dependsOnMethods={"checkCreatePost"})
	public void checkCreatedRecordIfPresent()
	{
		PostPojo postObj= new PostPojo();
		postObj.setTitle("Test Title");
		postObj.setBody("Test Body");
		postObj.setUserId(1);
		
		
		//Create New Record
		PostPojo respObj=given().
			spec(this.spec).
			body(postObj).
			contentType(ContentType.JSON).
		when().
			request().
			post(RESOURCE_UNDERTEST).as(PostPojo.class);
		
		
		Assert.assertEquals(respObj.getBody(), postObj.getBody());
		Assert.assertEquals(respObj.getTitle(), postObj.getTitle());
		Assert.assertEquals(respObj.getUserId(), postObj.getUserId());
		
		
		//Perform GET to get the newly created record
		PostPojo[] postObjs=
				
				given().
					spec(this.spec).
					param("userId",respObj.getUserId()).
					param("id",respObj.getId()).
				when().
					get(RESOURCE_UNDERTEST).as(PostPojo[].class);
		
		//Verify if the created record is present
		Assert.assertEquals(postObjs.length, 1);
		
	}
	
	/**
	 * Send Invalid JSON body - Service should return 400
	 */
	@Test
	public void createPostWithInvalidJson()
	{
		String json="{ \"hello\":\"invalid\"}";
		given().
			spec(this.spec).
			body(json).
			contentType(ContentType.JSON).
		when().
			request().
			post(RESOURCE_UNDERTEST).
		then().
			statusCode(400);
	}
	
	/**
	 * Verify update to existing records
	 * Method - PUT
	 */
	@Test
	public void updateRecord()
	{
		PostPojo putObj= new PostPojo();
		putObj.setTitle("Updated Title");
		putObj.setBody("Updated Body");
		putObj.setUserId(1);
		putObj.setId(1);
		
		given().
			spec(this.spec).
			body(putObj).
		when().
			request().
			put(RESOURCE_UNDERTEST+"/2").
		then().
			statusCode(200);
	}
	
	/**
	 * Verify if updated record is present
	 * This test will fail as per the Service Specification
	 * Note: the resource will not be really updated on the server but it will be faked as if.
	 */
	
	@Test(dependsOnMethods={"updateRecord"})
	public void checkUpdatedRecord()
	{
		PostPojo putObj= new PostPojo();
		putObj.setTitle("Updated Title");
		putObj.setBody("Updated Body");
		putObj.setUserId(1);
		putObj.setId(2);
		
		PostPojo putRespObj=
			given().
				spec(this.spec).
				body(putObj).
				contentType(ContentType.JSON).
			when().
				request().
				put(RESOURCE_UNDERTEST+"/2").as(PostPojo.class);
		
		Assert.assertEquals(putRespObj.getTitle(), putObj.getTitle());
		Assert.assertEquals(putRespObj.getBody(), putObj.getBody());
				
		//Check the updated record via GET
		PostPojo[] putObjs=
				
				given().
					spec(this.spec).
					param("userId",putRespObj.getUserId()).
					param("id",putRespObj.getId()).
				when().
					get(RESOURCE_UNDERTEST).as(PostPojo[].class);
		
		//Verify if the created record is present
		Assert.assertEquals(putObjs.length, 1);
		Assert.assertEquals(putObjs[0].getTitle(), putObj.getTitle());
		Assert.assertEquals(putObjs[0].getBody(), putObj.getBody());				
	}
	
	/**
	 * Verify if partial update of resource is working
	 * Method : PATCH
	 */
	@Test
	public void checkPartialUpdate()
	{
		PostPojo patchObj= new PostPojo();
		patchObj.setTitle("Updated Title");		
		
		given().
			spec(this.spec).
			body(patchObj).
			contentType(ContentType.JSON).
		when().
			request().
			patch(RESOURCE_UNDERTEST+"/1").
		then().
			statusCode(200);
	}
	
	
	/**
	 * Check the partially updated record
	 * This test will fail due to the service specification
	 * Note: the resource will not be really updated on the server but it will be faked as if.
	 */
	
	@Test(dependsOnMethods={"checkPartialUpdate"})
	public void checkPartiallyUpdatedData()
	{
		PostPojo patchObj= new PostPojo();
		patchObj.setTitle("Updated Title - Partial");		
		
		PostPojo patchRespObj=
		given().
			spec(this.spec).
			body(patchObj).
			contentType(ContentType.JSON).
		when().
			request().
			patch(RESOURCE_UNDERTEST+"/1").as(PostPojo.class);
		
		Assert.assertEquals(patchRespObj.getTitle(), patchObj.getTitle());
		Assert.assertEquals(patchRespObj.getId(), patchObj.getId());
		
		//As per service specification it will return 1 for user id
		//But its returning 0
		Assert.assertEquals(patchRespObj.getUserId(), 1);


		
		//Check the updated record via GET
		PostPojo[] pathObjs=
				
				given().
					spec(this.spec).
					param("userId",patchRespObj.getUserId()).
					param("id",patchRespObj.getId()).
				when().
					get(RESOURCE_UNDERTEST).as(PostPojo[].class);
				
		//Verify if the updated record is present
		Assert.assertEquals(pathObjs.length, 1);
		Assert.assertEquals(pathObjs[0].getTitle(), patchObj.getTitle());
		
	}
	
	/**
	 * Verify if Delete is working
	 * Method - DELETE
	 */
	@Test
	public void checkDeletePost()
	{
		given().
			spec(this.spec).
		when().
			delete(RESOURCE_UNDERTEST+"/1").
		then().
			statusCode(200);
	}
	
	
	/**
	 * Check if Deleted Record is removed from list
	 */
	
	@Test
	public void checkDeletedRecord()
	{
		given().
			spec(this.spec).
		when().
			delete(RESOURCE_UNDERTEST+"/2").
		then().
			statusCode(200);
		
		//Verify if service is returning 404
		//Since record is not found
		given().
			spec(this.spec).
		when().
			request().
			get(RESOURCE_UNDERTEST+"/2").
		then().
			statusCode(404);
		
	}
}
