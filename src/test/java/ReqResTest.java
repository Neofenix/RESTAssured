import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class ReqResTest extends BaseTest{

//    @Before
//    public void setup(){
////        RestAssured.baseURI = "https://reqres.in";
////        RestAssured.basePath = "/api";
////        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
////
////        logger.info("Iniciando la configuracion");
////        RestAssured.requestSpecification = new RequestSpecBuilder()
////                .setContentType(ContentType.JSON)
////                .build();
////        logger.info("Configuracion exitosa");
//        RestAssured.requestSpecification = defaultRequestSpecification();
//
//    }

    @Test
    public void loginTest(){
                 given()
                         //.spec(prodtRequestSpecification()) //set new spec
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token",notNullValue())
                .extract()
                .asString();

    }

    @Test
    public void getSingleUser(){
                 given()
                .get("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id",equalTo(2))
                .extract()
                .asString();

    }

    @Test
    public void deleteUserTest(){
                 given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void patchUserTest(){
        String nameUpdate =
        given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("name");

        assertThat(nameUpdate,equalTo("morpheus"));
    }

    @Test
    public void putUserTest(){
        String jobUpdate =
                given()
                        .when()
                        .body("{\n" +
                                "    \"name\": \"morpheus\",\n" +
                                "    \"job\": \"zion resident\"\n" +
                                "}")
                        .put("users/2")
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .jsonPath().getString("job");

        assertThat(jobUpdate,equalTo("zion resident"));
    }

    @Test
    public void getAllUserTest(){
        Response response =
                given()
                        .when()
                        .get("user?page=2");


        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        JsonPath body = response.getBody().jsonPath();
        String contentType = response.getContentType();

        assertThat(statusCode,equalTo(HttpStatus.SC_OK));
        System.out.println("body: " + body);
        System.out.println("content type: " + contentType);
        System.out.println("Headers: " + headers);
        System.out.println("***************");
        System.out.println("***************");
        System.out.println(headers.get("Content-Type"));
        System.out.println(headers.get("Transfer-Encoding"));
        System.out.println(body.getString("page"));
    }

    @Test
    public void getAllUserTest2(){

        JsonPath response =given()
                .when()
                .get("users?page=2")
                .then()
                .extract()
                .body()
                .jsonPath();
         //second form
        //JsonPath response =given().when().get("user?page=2").then().extract().body().asString();


        int page = response.get("page");  //from(response).get("page");
        int totalPages = response.get("total_pages");
        int idFirstUser = response.get("data[0].id");

        System.out.println("page" + page);
        System.out.println("totalPages" + totalPages);
        System.out.println("User" + idFirstUser);

        List<Map> userWithIdGreatherThan10 = response.get("data.findAll {user -> user.id > 10 }");
        String email = userWithIdGreatherThan10.get(0).get("email").toString();

        List<Map> user = response.get("data.findAll {user -> user.id > 10 && user.last_name == 'Howell'}");
        String id = user.get(0).get("id").toString();


    }

    @Test
    public void createUserTest() {

        JsonPath response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users")
                .then().extract().body().jsonPath();


        User user = response.getObject("",User.class); //Get the result of a Object path expression as a java Object
        System.out.println(user.getId());
        System.out.println(user.getJob());
    }

    @Test
    public void registerUserTest(){
        CreateUserRequest user = new CreateUserRequest();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");
        CreateUserResponse UserResponse=
                given()
                .when()
                .body(user)   //Serialize -> Take an object and transform it to JSON OR XML
                        .post("register")
                .then()
                        .spec(defaultResponseSpecification())  //Setear default specification
//                        .statusCode(200)  //Assertions in specific fields of response
//                        .contentType("application/json")
                .extract()
                        .body()
                        .as(CreateUserResponse.class);   //map resposne to java object


        assertThat(UserResponse.getId(),equalTo(4));
        assertThat(UserResponse.getToken(),equalTo("QpwL5tke4Pnpja7X4"));

    }
}
