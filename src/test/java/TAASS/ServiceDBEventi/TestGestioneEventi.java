package TAASS.ServiceDBEventi;

import TAASS.ServiceDBEventi.classiComode.IscriviEvento;
import TAASS.ServiceDBEventi.controllers.EventoController;
import TAASS.ServiceDBEventi.models.Evento;
//import TAASS.ServiceDBEventi.models.IscrittiEvento;
import TAASS.ServiceDBEventi.models.TipoEvento;
import TAASS.ServiceDBEventi.repositories.TipoEventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

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
    /*public void creaEvento() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        TipoEvento tipo = getTipoProva();
        URI uri = new URI("http://localhost:8080/api/v1/evento");
        Evento evento = new Evento("prova8", 50, 10, false,
                "evento di prova per verificare che i tipi siano giusti", "prova", tipo, null, 1, 1);
        HttpEntity<Evento> httpEntity = new HttpEntity<>(evento, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Evento> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                Evento.class);
        lastAdd = responseEntity.getBody().getId();
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody().toString());
    }*/

    public TipoEvento getTipoProva() throws URISyntaxException {
        TipoEvento tipo = null;
        URI uri = new URI("http://localhost:8080/api/v1/tipo-evento/getById/12");
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
    public void iscriviEvento()throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI uri = new URI("http://localhost:8080/api/v1/evento/iscrivi");
        Evento evento = getEventoByID();
        System.out.println("evento = " + evento.getId());
        long utente = 2;
        IscriviEvento iscriviEvento = new IscriviEvento(evento, utente);
        HttpEntity<IscriviEvento> httpEntity = new HttpEntity<>(iscriviEvento, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Evento> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity,
                Evento.class);
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody().toString());
    }

    @Test
    public void disiscriviEvento() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI uri = new URI("http://localhost:8080/api/v1/evento/disiscrivi");
        Evento evento = getEventoByID();
        System.out.println("evento = " + evento.getId());
        long utente = 1;
        IscriviEvento iscriviEvento = new IscriviEvento(evento, utente);
        HttpEntity<IscriviEvento> httpEntity = new HttpEntity<>(iscriviEvento, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity,
                String.class);
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody().toString());
    }

    private Evento getEventoByID() throws URISyntaxException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI uri = new URI("http://localhost:8080/api/v1/evento/info-evento/11");
        HttpEntity<Evento> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Evento> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
                Evento.class);

        Evento evento = responseEntity.getBody();
        System.out.println("getEventoByID: evento = "+ evento);
        return evento;
    }

    /*@Test
    public void modificaEvento() throws URISyntaxException{
        System.out.println("prova modifica evento");
        System.out.println("creo l'evento da modificare");
        creaEvento();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //headers.add("id", "6");

        Evento evento = new Evento("modificato", 20, 2, false,
                "evento di prova modificato", "modifica", new TipoEvento(), null, 1, 1);*/
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
    /*
        URI uri = new URI("http://localhost:8080/api/v1/evento/aggiorna/" + lastAdd);
        HttpEntity<Evento> httpEntity = new HttpEntity<>(evento, headers);


        System.out.println(httpEntity.getHeaders());
        System.out.println(httpEntity.getBody());

        ResponseEntity<Evento> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity,
                Evento.class);
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println("Status value: " + responseEntity.getStatusCodeValue());
    }*/

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

    /*@Test
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
    }*/

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
