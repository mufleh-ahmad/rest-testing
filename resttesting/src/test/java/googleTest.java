import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Test;

import static io.restassured.RestAssured.given;

/**
 * Created by Mufleh on 28/04/2019.
 */
public class googleTest {

    @Test
    public void getTest() {
        RestAssured.baseURI="https://maps.googleapis.com/";

        given().
                param("location","52.517779,-1.923983").
                param("radius","500").
                param("key", "AIzaSyCI7PVQhgG1ypZnOAUVtSL_ynGrkoLOhcA").
                when().
                get("maps/api/place/nearbysearch/json").
                then()
                .assertThat().statusCode(200).and().contentType(ContentType.JSON).and()
                .body("results[0].name", equalTo("Birmingham")).and()
                .body("results[0].place_id", equalTo("ChIJc3FBGy2UcEgRmHnurvD-gco"));
    }
}
