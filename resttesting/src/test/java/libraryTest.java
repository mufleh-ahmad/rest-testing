import Utils.Constants;
import Utils.JSONUtil;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;

/**
 * Created by Mufleh on 17/06/2019.
 */
public class libraryTest {

    static Properties properties = new Properties();
    static Logger log = LogManager.getLogger(libraryTest.class.getName());
    List<String> idList = new ArrayList<>();

    @BeforeClass
    public static void setup() throws IOException {
        FileInputStream fis= new FileInputStream(System.getProperty("user.dir")+"\\env.properties");
        properties.load(fis);
    }

    @Test(dataProvider = "BookData")
    public void addLibraryBooks(String isbn, String aisle) throws IOException {
        log.info("Test starting");
        log.warn("warning message");
        File file = new File(getClass().getClassLoader().getResource("addLibrary.json").getFile());
        String testData = updateAddBookDynamicField(file,isbn,aisle);
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
        idList.add(id);
        for (String s : idList) {
            System.out.println(s);
        }
    }

    @Test
    public void deleteLibraryBooks() throws IOException, InterruptedException {
        Thread.sleep(10000);
        File file = new File(getClass().getClassLoader().getResource("deleteLibrary.json").getFile());

        for (String id : idList) {
            String testData = updateDeleteBookDynamicField(file,id);
            RestAssured.baseURI= String.valueOf(properties.get("LIBRARY_HOST"));
            Response response =  given().
                    header("Content-Type","application/json").
                    body(testData).
                    when().
                    post(Constants.DELETE_LIBRARY_RESOURCE).
                    then().assertThat().statusCode(200).
                    extract().response();
        }
    }

    public String updateAddBookDynamicField(File fileName, String isbn, String aisle) throws IOException {
        String uniqueSuffix = RandomStringUtils.randomAlphabetic(5);
        String testdataJson = FileUtils.readFileToString(fileName);
        testdataJson = testdataJson.replaceAll("ISBN",isbn+uniqueSuffix);
        testdataJson = testdataJson.replaceAll("AISLE",aisle+uniqueSuffix);
        return testdataJson;
    }

    public String updateDeleteBookDynamicField(File fileName, String id) throws IOException {
        String testdataJson = FileUtils.readFileToString(fileName);
        testdataJson = testdataJson.replaceAll("id",id);
        return testdataJson;
    }

    @DataProvider(name="BookData")
    public Object[][] getData(){
        return new Object[][]{{"isbn123","78"},{"isbn124","12"},{"isbn345","3"}};
    }

}
