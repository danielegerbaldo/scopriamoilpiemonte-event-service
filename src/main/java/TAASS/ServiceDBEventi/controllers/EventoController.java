package TAASS.ServiceDBEventi.controllers;

import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/evento")
public class EventoController {
    /*TODO:
        -eventi per nome
        -eventi di un comune
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

    //TODO: verificarne la correttezza
    @GetMapping("/info-evento/{id}")
    public Evento trovaPerID(@PathVariable long id){
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.get();
    }

    @GetMapping("/eventi-comune/{comune}")
    public List<Evento> eventiDelComune(@PathVariable long comune){
        List<Evento> eventi = eventoRepository.findByComune(comune);
        return eventi;
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
            return new ResponseEntity<>(eventoRepository.save(_evento), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
