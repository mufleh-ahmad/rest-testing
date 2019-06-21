import Utils.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import Utils.Constants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * Created by Mufleh on 28/04/2019.
 */
public class googleTest {

    static Properties properties = new Properties();

    @BeforeClass
    public static void setup() throws IOException {
        FileInputStream fis= new FileInputStream(System.getProperty("user.dir")+"\\env.properties");
        properties.load(fis);

    }

    @Test
    public void getMethod() {
        RestAssured.baseURI= String.valueOf(properties.get("HOST"));
        given().
                param("location","52.517779,-1.923983").
                param("radius","500").
                param("key", String.valueOf(properties.get("HOST"))).
                when().
                get(Constants.NEARBY_SEARCH_RESOURCE).
                then()
                .assertThat().statusCode(200).and().contentType(ContentType.JSON).and()
                .body("results[0].name", equalTo("Birmingham")).and()
                .body("results[0].place_id", equalTo("ChIJc3FBGy2UcEgRmHnurvD-gco")).and()
                .header("Server", "scaffolding on HTTPServer2");
    }

    @Test
    public void postTest() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("test1.json").getFile());
        // grab response
        RestAssured.baseURI=String.valueOf(properties.get("HOST2"));
        Response response = given().
                queryParam("key",String.valueOf(properties.get("KEY2"))).body(file).
                when().
                post(Constants.ADD_RESOURCE).
                then().assertThat().statusCode(200).and().body("status",equalTo("OK")).
                extract().response();

        // grab the place id
        String actualResponse = response.asString();
        JsonPath js = new JsonPath(actualResponse);
        String placeId = js.get("place_id");
        String content = updateFile(placeId,"test2.json");
        given().
                queryParam("key",String.valueOf(properties.get("KEY2"))).body(content).
                when().
                post(Constants.DELETE_RESOURCE).
                then().assertThat().statusCode(200).and().body("status",equalTo("OK"));
    }

    @Test
    public void getResultsAndParse() throws JsonProcessingException {
        RestAssured.baseURI= String.valueOf(properties.get("HOST"));

        Response response = given().
                param("location","52.517779,-1.923983").
                param("radius","500").
                param("key", String.valueOf(properties.get("KEY"))).
                when().
                get(Constants.NEARBY_SEARCH_RESOURCE).
                then()
                .assertThat().statusCode(200).extract().response();

        JsonPath results = JSONUtil.rawToJson(response);

        int count = results.get("results.size");

        for (int i = 0; i < count; i++) {
            String test = results.get("results["+i+"].name");
            System.out.println(test);
        }
    }

    @Test
    public void logAll() throws JsonProcessingException {
        RestAssured.baseURI= String.valueOf(properties.get("HOST"));

        Response response = given().
                param("location","52.517779,-1.923983").
                param("radius","500").
                param("key", String.valueOf(properties.get("KEY"))).log().all().
                when().
                get(Constants.NEARBY_SEARCH_RESOURCE).
                then()
                .assertThat().statusCode(200).extract().response();
    }

    @Test
    public void logWhenFailed() {

        RestAssured.baseURI= String.valueOf(properties.get("HOST"));
        given().
                param("location","52.517779,-1.923983").
                param("radius","500").
                param("key", String.valueOf(properties.get("KEY"))).
                when().
                get(Constants.NEARBY_SEARCH_RESOURCE).
                then().log().ifValidationFails()
                .assertThat().statusCode(201).and().contentType(ContentType.JSON).and()
                .body("results[0].name", equalTo("Birmingham")).and()
                .body("results[0].place_id", equalTo("ChIJc3FBGy2UcEgRmHnurvD-gco"));
    }



    private static String updateFile(String placeId,String fileName) throws IOException {
        Path path = Paths.get("src\\test\\resources\\"+fileName);
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("PLACE_ID", placeId);
        return content;
    }
}
