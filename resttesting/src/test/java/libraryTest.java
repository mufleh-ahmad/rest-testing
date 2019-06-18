import Utils.Constants;
import Utils.JSONUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import sun.nio.cs.StandardCharsets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

/**
 * Created by Mufleh on 17/06/2019.
 */
public class libraryTest {

    static Properties properties = new Properties();

    @BeforeClass
    public static void setup() throws IOException {
        FileInputStream fis= new FileInputStream("C:\\Users\\Mufleh\\git\\rest-testing\\resttesting\\src\\test\\resources\\env.properties");
        properties.load(fis);

    }

    @Test
    public void addLibraryBooks() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("addLibrary.json").getFile());
        String testData = updateDynamicField(file,"ISBN1234","A123");
        RestAssured.baseURI= String.valueOf(properties.get("LIBRARY_HOST"));
        Response response =  given().
                header("Content-Type","application/json").
                body(testData).
                when().
                post(Constants.ADD_LIBRARY_RESOURCE).
                then().assertThat().statusCode(200).
                extract().response();

        JsonPath js = JSONUtil.rawToJson(response);
        String id = js.get("ID");
        System.out.println(id);
    }

    public String updateDynamicField(File fileName, String isbn, String aisle) throws IOException {
        String uniqueSuffix = RandomStringUtils.randomAlphabetic(5);
        String testdataJson = FileUtils.readFileToString(fileName);
        testdataJson = testdataJson.replaceAll("ISBN",isbn+uniqueSuffix);
        testdataJson = testdataJson.replaceAll("AISLE",aisle+uniqueSuffix);
        return testdataJson;
    }

}
