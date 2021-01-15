package TAASS.ServiceDBEventi;

import TAASS.ServiceDBEventi.controllers.IscrittiEventoController;
import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.models.IscrittiEvento;
import TAASS.ServiceDBEventi.models.TipoEvento;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

class Comoda{
    public long eventoID;
    public long utenteID;

    public Comoda(long eventoID, long utenteID) {
        this.eventoID = eventoID;
        this.utenteID = utenteID;
    }
}

public class TestIscrizione {
    @Autowired
    IscrittiEventoController iscrittiEventoController;

    @Test
    public void registrati() throws URISyntaxException {
        //TODO: creare una classe comoda per risolvere
        /*
        2021-01-03 12:11:01.127  WARN 444 --- [nio-8080-exec-2] .w.s.m.s.DefaultHandlerExceptionResolver :
        Resolved [org.springframework.beans.ConversionNotSupportedException: Failed to convert property value
        of type 'java.lang.Long' to required type 'TAASS.ServiceDBEventi.models.Evento' for property 'evento';
        nested exception is java.lang.IllegalStateException: Cannot convert value of type 'java.lang.Long' to
        required type 'TAASS.ServiceDBEventi.models.Evento' for property 'evento': no matching editors or conversion strategy found]

         */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Evento evento = getEvento();
        URI uri = new URI("http://localhost:8080/api/v1/iscritti-evento/iscriviAdEvento");
        IscrittiEvento ie = new IscrittiEvento(1, evento);
        toJSON(ie);
        Comoda c = new Comoda(ie.getEvento().getId(), ie.getUtenteID());
        toJSON(c);
        System.out.println(c.eventoID);
        HttpEntity<IscrittiEvento> httpEntity = new HttpEntity<>(ie, headers);
        System.out.println(httpEntity);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<IscrittiEvento> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                IscrittiEvento.class);
    }

    private void toJSON(IscrittiEvento ie){
        ObjectMapper mapper = new ObjectMapper();
        String ieJSON = "[]";
        try {
            ieJSON = mapper.writeValueAsString(ie);
            System.out.println(ieJSON);
        } catch (JsonProcessingException e) {
            System.out.println("errore nella conversione a JSON");
        }
    }

    private void toJSON(Comoda ie){
        ObjectMapper mapper = new ObjectMapper();
        String ieJSON = "[]";
        try {
            ieJSON = mapper.writeValueAsString(ie);
            System.out.println(ieJSON);
        } catch (JsonProcessingException e) {
            System.out.println("errore nella conversione a JSON");
        }
    }

    private Evento getEvento() throws URISyntaxException {
        URI uri = new URI("http://localhost:8080/api/v1/evento/info-evento/8");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Evento> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Evento> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                Evento.class);
        System.out.println("getEvento: ");
        System.out.println(responseEntity.getBody());
        return responseEntity.getBody();
    }
}
