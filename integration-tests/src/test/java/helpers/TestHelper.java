package helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class TestHelper {

    public static final String BASE_URI = "http://localhost:4004";
    public static final String VALID_EMAIL = "testuser@test.com";
    public static final String VALID_PASSWORD = "password123";

    public static void setupBaseUri() {
        RestAssured.baseURI = BASE_URI;
    }

    public static String getValidAuthToken() {
        String loginPayload = String.format("""
                {
                    "email": "%s",
                    "password": "%s"
                }
                """, VALID_EMAIL, VALID_PASSWORD);

        return given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }

    public static String createLoginPayload(String email, String password) {
        return String.format("""
                {
                    "email": "%s",
                    "password": "%s"
                }
                """, email, password);
    }
}