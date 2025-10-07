import helpers.TestHelper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    @BeforeAll
    static void setup() {
        TestHelper.setupBaseUri();
    }

    @Test
    public void shouldReturnOkWithValidToken() {
        String loginPayload = TestHelper.createLoginPayload(
                TestHelper.VALID_EMAIL,
                TestHelper.VALID_PASSWORD
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("data.token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated token: " + response.jsonPath().getString("data.token"));
    }

    @Test
    public void shouldReturnUnAuthorizedOrInvalidLogin() {
        String loginPayload = TestHelper.createLoginPayload(
                "whoisyougang@test.com",
                "wrongpassword"
        );

        given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

}
