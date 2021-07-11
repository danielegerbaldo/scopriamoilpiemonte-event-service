package TAASS.ServiceDBEventi.controllers;

import TAASS.ServiceDBEventi.models.TipoEvento;
import TAASS.ServiceDBEventi.repositories.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tipo-evento")
public class TipoEventoController {
    @Autowired
    TipoEventoRepository tipoEventoRepository;

    @GetMapping
    public List<TipoEvento> getAllTipi(){
        System.out.println("/api/v1/tipo-evento");
        List<TipoEvento> tipi = new ArrayList<>();
        tipi = tipoEventoRepository.findAll();
        System.out.println("Lista tipi evento: " + tipi.size());
        return tipi;
    }

    @GetMapping("/getById/{id}")
    public TipoEvento getTipoPerId(@PathVariable long id){
        return tipoEventoRepository.findById(id).get();
    }

    @PostMapping
    public TipoEvento addTipo(@RequestBody TipoEvento tipoEvento){
        TipoEvento nuovoTipo = tipoEventoRepository.save(new TipoEvento(tipoEvento.getNome(), tipoEvento.getDescrizione()));
        return nuovoTipo;
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> rimuoviTuttiTipi(){
        tipoEventoRepository.deleteAll();
        return new ResponseEntity<>("tutti i tipi di eventi sono stati cancellati con successo", HttpStatus.OK);
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity<String> rimuoviTipoPerId(@RequestBody long id){
        tipoEventoRepository.deleteById(id);
        return new ResponseEntity<>("il tipo evento con ID = " + id + "e' stato rimosso", HttpStatus.OK);
    }


}
