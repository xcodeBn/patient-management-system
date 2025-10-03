import helpers.TestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
    static void setUp() {
        TestHelper.setupBaseUri();
    }

    @Test
    public void shouldReturnPatientWithValidToken() {
        String token = TestHelper.getValidAuthToken();

        given().header("Authorization", "Bearer " + token)
                .when().get("/api/patients")
                .then()
        .statusCode(200)
                .body("patients",notNullValue());


    }

}


