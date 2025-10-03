import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    @BeforeAll
    static  void setup() {
        //api gateway
        RestAssured.baseURI = "http://localhost:4004";
    }
    @Test
    public void shouldReturnOkWithValidToken(){

        String loginPayload = """
                {
                    "email" : "testuser@test.com",
                    "password" : "password123" 
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token",notNullValue())
                .extract()
                .response();

        System.out.println("Generated token: " + response.jsonPath().getString("token"));

    }

    @Test
    public void shouldReturnUnAuthorizedOrInvalidLogin(){

        String loginPayload = """
                {
                    "email" : "whoisyougang@test.com",
                    "password" : "wrongpassword" 
                }
                """;

         given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

}
