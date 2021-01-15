package TAASS.ServiceDBEventi.controllers;

import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.models.IscrittiEvento;
import TAASS.ServiceDBEventi.repositories.EventoRepository;
import TAASS.ServiceDBEventi.repositories.IscrittiEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/iscritti-evento")
public class IscrittiEventoController {

    @Autowired
    private IscrittiEventoRepository iscrittiEventoRepository;

    @GetMapping("/getIscrittiEvento/{evento}")
    public List<Long> getAllIscrittiOfEvento(@PathVariable long eventoID){
        //prendo da tutti gli elementi nella tabella e in cui eventoID e restituisce tutti
        //gli id degli iscritti
        List<Long> iscritti = new ArrayList<>();
        List<IscrittiEvento> ie = iscrittiEventoRepository.findByEvento(eventoID);
        for (IscrittiEvento i:ie) {
            iscritti.add(i.getUtenteID());
        }
        return iscritti;
    }

    @GetMapping("/getIscrizioniUtente/{utente}")
    public List<Evento> getAllIscrizioniUtenti(@PathVariable long utenteID){
        List<IscrittiEvento> iscrizioniUtente = iscrittiEventoRepository.findByUtenteID(utenteID);
        List<Evento> eventi = new ArrayList<>();
        for (IscrittiEvento i : iscrizioniUtente){
            eventi.add(i.getEvento());
        }
        return eventi;
        //return null;
    }



    @PostMapping("/iscriviAdEvento")    //TODO: non è finito
    public IscrittiEvento iscrivi(@RequestBody IscrittiEvento iscritto){
        //TODO: aggiungere un controllo che venga effettivamente aggiunto
        System.out.println("iscrivi");
        System.out.println(iscritto.getEvento().getId() + "; " + iscritto.getEvento().getNome());
        System.out.println(iscritto.getUtenteID());
        return (iscrittiEventoRepository.save(iscritto));
    }

    /*@Transactional
    public void registraIscrizione(Person person) {
        EntityManager entityManager = null;
        entityManager.createNativeQuery("INSERT INTO person (id, first_name, last_name) VALUES (?,?,?)")
                .setParameter(1, person.getId())
                .setParameter(2, person.getFirstName())
                .setParameter(3, person.getLastName())
                .executeUpdate();
    }*/

    @PostMapping("/disiscrivitiEvento") //TODO: non è finito
    public ResponseEntity<String> disiscrivi(@RequestBody IscrittiEvento iscritto){
        //iscrittiEventoRepository.deleteRegistrazione(iscritto.getUtenteID(), iscritto.getEventoID());
        return new ResponseEntity<>("Disiscrizione avvenuta con successo", HttpStatus.OK);
    }




}
