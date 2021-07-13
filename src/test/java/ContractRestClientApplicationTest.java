import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class ContractRestClientApplicationTest {
    @RegisterExtension
    public StubRunnerExtension stubRunner = new StubRunnerExtension()
            .downloadStub("com.example", "contract-rest-service", "0.0.1-SNAPSHOT", "stubs")
            .stubsMode(StubRunnerProperties.StubsMode.LOCAL);

    private String accessToken = "";

    @Test
    public void login() throws JSONException {
        Object n = "flavio@gmail.com";
        Object p = "flavio";
        // create request body
        JSONObject request = new JSONObject();
        try{
            request.put("email", n);
            request.put("password", p);
        }catch (JSONException e){
            System.out.println("Error JSON");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
        String urlString = "http://localhost/api/v1/login";
        // Ottengo LOGIN
        ResponseEntity<String> loginResponse = new RestTemplate()
                .exchange(urlString, HttpMethod.POST, entity, String.class);
        //Controllo se è andata bene
        if (loginResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("token: "+loginResponse.getBody());
        } else if (loginResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            System.out.println("NON AUTORIZZATO");
        }
        JSONObject jsonObject = new JSONObject(loginResponse.getBody());
        this.accessToken = jsonObject.getString("accessToken");
    }
    @Test
    public void getAllEvents() throws JSONException {
        this.login();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers = new HttpHeaders();
        String url="http://localhost/api/v1/evento/";
        System.out.println("MyAccessToken: " + accessToken);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> eventoResponseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (eventoResponseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println(eventoResponseEntity.getBody());
        } else if (eventoResponseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            System.out.println("NON AUTORIZZATO");
        }
    }
}
