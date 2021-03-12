package TAASS.ServiceDBEventi.controllers;

import TAASS.ServiceDBEventi.classiComode.IscriviEvento;
import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/evento")
public class EventoController {
    /*TODO:
        -eventi a cui partecipa una persona
        -eventi di un tipo
        -eventi di un tipo in un comune
     */

    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public List<Evento> getAllEventi(){
        List<Evento> eventi = new ArrayList<>();
        eventoRepository.findAll().forEach(eventi::add);
        return eventi;
    }

    @GetMapping("/nome/{nome}")
    public List<Evento> trovaPerNome(@PathVariable String nome){
        List <Evento> eventi = eventoRepository.findByNome(nome);
        return eventi;
    }

    @GetMapping("/tipo/{tipo}")
    public List<Evento> trovaPerTipo(@PathVariable long tipo){
        System.out.println(">>>trovaPerTipo: tipo richiesto: " + tipo);
        List<Evento> eventi = eventoRepository.findByTipoEventoId(tipo);
        System.out.println(">>>trovaPerTipo: elementi trovati: " + eventi.size());
        return eventi;
    }

    @GetMapping("/info-evento/{id}")
    public Evento trovaPerID(@PathVariable long id){
        System.out.println(">>>trovaPerID: id = " + id);
        Optional<Evento> evento = eventoRepository.findById(id);
        if(evento.isPresent()){
            System.out.println(">>>trovaPerID: esiste evento " + id);
            return evento.get();
        }else{
            System.out.println(">>>trovaPerID: NON esiste evento " + id);
            return null;
        }

    }

    @GetMapping("/iscritti-evento/{id}")
    public HashSet<Long> iscrittiEvento(@PathVariable long id){
        Optional<Evento> iscritti = eventoRepository.findById(id);
        if(iscritti.isPresent()){
            return (HashSet<Long>) iscritti.get().getIscritti();
        }else{
            return null;
        }

    }

    @GetMapping("/eventi-comune/{comune}")
    public List<Evento> eventiDelComune(@PathVariable long comune){
        List<Evento> eventi = eventoRepository.findByComune(comune);
        return eventi;
    }


    @DeleteMapping("/disiscrivi")
    public ResponseEntity<String> disiscrivi(@RequestBody IscriviEvento iscriviEvento){
        Evento evento = iscriviEvento.getEvento();
        long utenteID = iscriviEvento.getUtente();
        if(evento.getIscritti().contains(utenteID)){
            evento.getIscritti().remove(utenteID);
            eventoRepository.save(evento);
            return new ResponseEntity<>("Utente <" + utenteID + "> disiscritto dall'evento " + evento.getId(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("L'utente <" + utenteID + "> non risulta iscritto all'evento " + evento.getId(), HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> rimuoviTuttiEventi(){
        eventoRepository.deleteAll();
        return new ResponseEntity<>("tutti gli eventi sono stati cancellati con successo", HttpStatus.OK);
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity<String> rimuoviUtentePerID(@RequestBody long id/*@PathVariable long  id*/){
        eventoRepository.deleteById(id);
        return new ResponseEntity<>("l'evento con ID = " + id + "e' stato rimosso", HttpStatus.OK);
    }

    @PostMapping
    public Evento addEvento(@RequestBody Evento evento){
        System.out.println(">>>addEvento: aggiungo evento: " + evento);
        Evento nuovoEvento = eventoRepository.save(new Evento(evento.getNome(), evento.getNumMaxPartecipanti(),
                evento.getPartecipanti(), evento.isStreaming(), evento.getDescrizione(), evento.getNote(),
                evento.getTipoEvento(), evento.getData(), evento.getProprietario(), evento.getComune()));
        return nuovoEvento;
    }

    @PostMapping("/iscrivi")
    public ResponseEntity<Evento> iscrivi(@RequestBody IscriviEvento iscriviEvento){
        System.out.println(">>>iscrivi: Ho ricevuto: evento = " + iscriviEvento.getEvento().getId() + "; utente = " + iscriviEvento.getUtente());
        iscriviEvento.getEvento().getIscritti().add(iscriviEvento.getUtente());
        return new ResponseEntity<>(eventoRepository.save(iscriviEvento.getEvento()), HttpStatus.OK);
        //return new ResponseEntity<>("Utente <" + iscriviEvento.getUtente() + "> iscritto all'evento " + iscriviEvento.getEvento().getId(), HttpStatus.OK);
    }

    //TODO: verificare se si può creare in maniera più "bella"
    @PutMapping("aggiorna/{id}")
    public ResponseEntity<Evento> aggiornaEvento(@PathVariable("id") long id, @RequestBody Evento evento){
        System.out.println(">>>aggiornaEvento: ricevuto id = " + id);
        Optional<Evento> datiEvento = eventoRepository.findById(id);

        if(datiEvento.isPresent()){
            Evento _evento = datiEvento.get();

            _evento.setNome(evento.getNome());
            _evento.setNumMaxPartecipanti(evento.getNumMaxPartecipanti());
            _evento.setPartecipanti(evento.getPartecipanti());
            _evento.setStreaming(evento.isStreaming());
            _evento.setDescrizione(evento.getDescrizione());
            _evento.setNote(evento.getNote());
            _evento.setTipoEvento(evento.getTipoEvento());
            _evento.setData(evento.getData());
            _evento.setProprietario(evento.getProprietario());
            _evento.setIscritti(evento.getIscritti());
            return new ResponseEntity<>(eventoRepository.save(_evento), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
