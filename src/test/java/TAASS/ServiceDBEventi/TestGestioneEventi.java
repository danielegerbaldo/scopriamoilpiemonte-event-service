package TAASS.ServiceDBEventi;

import TAASS.ServiceDBEventi.controllers.EventoController;
import TAASS.ServiceDBEventi.controllers.TipoEventoController;
import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.models.IscrittiEvento;
import TAASS.ServiceDBEventi.models.TipoEvento;
import TAASS.ServiceDBEventi.repositories.EventoRepository;
import TAASS.ServiceDBEventi.repositories.IscrittiEventoRepository;
import TAASS.ServiceDBEventi.repositories.TipoEventoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.parser.JSONParser;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


public class TestGestioneEventi {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private long lastAdd = 0;

    @Autowired EventoController eventoController;

    @Autowired
    TipoEventoRepository tipoEventoRepository;

    @Autowired MockMvc mockMvc;

    @Test
    public void creaEvento() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        TipoEvento tipo = getTipoProva();
        URI uri = new URI("http://localhost:8080/api/v1/evento");
        Evento evento = new Evento("prova2", 10, 1, false,
                "evento di prova", "prova", tipo, null, 1, 2);
        HttpEntity<Evento> httpEntity = new HttpEntity<>(evento, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Evento> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                Evento.class);
        lastAdd = responseEntity.getBody().getId();
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody().toString());
    }

    public TipoEvento getTipoProva() throws URISyntaxException {
        TipoEvento tipo = null;
        URI uri = new URI("http://localhost:8080/api/v1/tipo-evento/getById/7");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TipoEvento> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TipoEvento> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                TipoEvento.class);
        tipo = responseEntity.getBody();
        return tipo;
    }

    @Test
    public void modificaEvento() throws URISyntaxException{
        System.out.println("prova modifica evento");
        System.out.println("creo l'evento da modificare");
        creaEvento();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //headers.add("id", "6");

        Evento evento = new Evento("modificato", 20, 2, false,
                "evento di prova modificato", "modifica", new TipoEvento(), null, 1, 1);
        /*
        ObjectMapper mapper = new ObjectMapper();
        String eventoJSON = "[]";
        try {
            eventoJSON = mapper.writeValueAsString(evento);
            System.out.println(eventoJSON);
        } catch (JsonProcessingException e) {
            System.out.println("errore nella conversione a JSON");
        }
        */
        URI uri = new URI("http://localhost:8080/api/v1/evento/aggiorna/" + lastAdd);
        HttpEntity<Evento> httpEntity = new HttpEntity<>(evento, headers);


        System.out.println(httpEntity.getHeaders());
        System.out.println(httpEntity.getBody());

        ResponseEntity<Evento> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity,
                Evento.class);
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println("Status value: " + responseEntity.getStatusCodeValue());
    }

    @Test
    public void cancellaEvento() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI("http://localhost:8080/api/v1/evento/deleteById");
        HttpEntity<Long> httpEntity = new HttpEntity<>(lastAdd, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity,
                String.class);
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println("Status value: " + responseEntity.getStatusCodeValue());
    }

    @Test
    public void cancellaTutti() throws URISyntaxException {
        System.out.println("test cancello tutti gli eventi");
        System.out.println("creo due eventi");
        creaEvento();
        creaEvento();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI("http://localhost:8080/api/v1/evento/deleteAll");
        HttpEntity<Long> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity,
                String.class);
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println("Status value: " + responseEntity.getStatusCodeValue());
    }

}

/*
@Test
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		URI uri = new URI("http://localhost:8080/book");
		Book book = new Book();
		book.setName("Learning Spring");
		book.setWriter("Krishna");

		HttpEntity<Book> httpEntity = new HttpEntity<Book>(book, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Book> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
				Book.class);

		System.out.println("Status Code: " + responseEntity.getStatusCode());
		System.out.println("Location: " + responseEntity.getHeaders().getLocation());
 */
