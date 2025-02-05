package ro.anaf;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import com.sdl.selenium.web.utils.Utils;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.http.Method.POST;

public class Controller {

    private static final String ANAF_BASE_PATH = "https://webservicesp.anaf.ro/api/registruroefactura/v1/interogare";

    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";

    public static void main(String[] args) {
        CreateRequest request = new CreateRequest(List.of(new Fields(26392200, "2025-01-29")));
        Response response = doRequest(POST, "", request.toString());
        String prettyString = response.getBody().asPrettyString();

        Utils.sleep(1);
    }

    private static Response doRequest(Method method, String path, String body) {
        RequestSpecification requestSpecification = given().contentType(CONTENT_TYPE);
//        requestSpecification.header("Authorization", buildAuthorizationString());
        if (Objects.nonNull(body)) {
            requestSpecification.body(body);
        }
        path = ANAF_BASE_PATH + path;
//        log.info("Will execute {} request on path {}", method, path);
        return switch (method) {
            case GET -> requestSpecification.when().get(path);
            case POST -> requestSpecification.when().post(path);
            case PUT -> requestSpecification.when().put(path);
            case DELETE -> requestSpecification.when().delete(path);
            case HEAD, TRACE, OPTIONS, PATCH -> throw new RuntimeException(method + "method is not supported!");
        };
    }

//    private static String buildAuthorizationString() {
//        String toEncode = JiraController.EMAIL + ":" + JiraController.TOKEN;
//        byte[] encodedBytes = Base64.getEncoder().encode(toEncode.getBytes());
//        return "Basic " + new String(encodedBytes);
//    }
}
