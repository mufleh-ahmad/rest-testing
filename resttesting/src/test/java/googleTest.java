import Utils.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static io.restassured.RestAssured.given;

/**
 * Created by Mufleh on 28/04/2019.
 */
public class googleTest {

    static Properties properties = new Properties();

    @BeforeClass
    public static void setup() throws IOException {
        FileInputStream fis= new FileInputStream("C:\\Users\\Mufleh\\git\\rest-testing\\resttesting\\src\\test\\resources\\env.properties");
        properties.load(fis);

    }

    @Test
    public void test1() {
        RestAssured.baseURI= String.valueOf(properties.get("HOST"));

        given().
                param("location","52.517779,-1.923983").
                param("radius","500").
                param("key", String.valueOf(properties.get("HOST"))).
                when().
                get("maps/api/place/nearbysearch/json").
                then()
                .assertThat().statusCode(200).and().contentType(ContentType.JSON).and()
                .body("results[0].name", equalTo("Birmingham")).and()
                .body("results[0].place_id", equalTo("ChIJc3FBGy2UcEgRmHnurvD-gco")).and()
                .header("Server", "scaffolding on HTTPServer2");
    }

    @Test
    public void test2() throws IOException {

        File file = new File(getClass().getClassLoader().getResource("test1.json").getFile());

        // grab response
        RestAssured.baseURI=String.valueOf(properties.get("HOST2"));
        Response response = given().
                queryParam("key",String.valueOf(properties.get("KEY2"))).body(file).
                when().
                post("maps/api/place/add/json").
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
                post("maps/api/place/delete/json").
                then().assertThat().statusCode(200).and().body("status",equalTo("OK"));
    }

    private static String updateFile(String placeId,String fileName) throws IOException {
        Path path = Paths.get("src\\test\\resources\\"+fileName);
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("PLACE_ID", placeId);
        return content;
    }
}
