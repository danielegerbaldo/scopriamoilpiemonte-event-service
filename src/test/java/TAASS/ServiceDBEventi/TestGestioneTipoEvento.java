package TAASS.ServiceDBEventi;

import TAASS.ServiceDBEventi.controllers.TipoEventoController;
import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.models.TipoEvento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class TestGestioneTipoEvento {
    @Autowired
    TipoEventoController tipoEventoController;

    private long lastAdd = 0;

    @Test
    public void creaTipo() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI("http://localhost:8080/api/v1/tipo-evento");
        TipoEvento tipoEvento = new TipoEvento("prova", "evento di prova per verificare che tutto funzioni");
        HttpEntity<TipoEvento> httpEntity = new HttpEntity<>(tipoEvento, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TipoEvento> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                TipoEvento.class);
        lastAdd = responseEntity.getBody().getId();
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody().toString());
    }
}
