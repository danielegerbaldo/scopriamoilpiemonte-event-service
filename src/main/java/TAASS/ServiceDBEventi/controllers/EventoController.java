package TAASS.ServiceDBEventi.controllers;

import TAASS.ServiceDBEventi.classiComode.IscriviEvento;
import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @GetMapping("/non-scaduti")
    public List<Evento> getEventiNonScaduti(){
        List<Evento> eventi;
        Date date =ottieniData();
        System.out.println("# eventi non scaduti: data attuale: " + date);
        eventi = eventoRepository.findTuttiEventiNonScaduti(date);
        return eventi;
    }

    @GetMapping("/eventi-non-iscritto/{utenteID}")
    public List<Evento> getEventiUtenteNonIscritto(@PathVariable long utenteID ){
        System.out.println("# getEventiUtenteNonIscritto: uid: " + utenteID);
        List<Evento> eventi = eventoRepository.findEventiUtenteNonIscritto(utenteID);
        System.out.println("# getEventiUtenteNonIscritto: size: " + eventi.size());
        return eventi;
    }

    @GetMapping("/eventi-non-iscritto-non-scaduti/{utenteID}")
    public List<Evento> getEventiUtenteNonIscrittoNonscaduti(@PathVariable long utenteID ){
        System.out.println("# getEventiUtenteNonIscritto: uid: " + utenteID);
        //start debug
        //stampa tutti gli iscritti a tutti gli eventi
        List<Evento> tuttiEventi = getAllEventi();
        System.out.println("#\tlista iscrizioni eventi");
        for(int i = 0; i < tuttiEventi.size(); i++){
            System.out.println("#\t\tevento: " + tuttiEventi.get(i).getId() + ", nome = " + tuttiEventi.get(i).getNome());
            ArrayList<Long> iscritti = new ArrayList<Long> (tuttiEventi.get(i).getIscritti()) ;
            for(int j = 0; j < iscritti.size(); j++ ){
                System.out.println("#\t\t\t-" +  iscritti.get(j));
            }
        }
        //stampa tutti gli eventi a cui non sono iscritto
        List<Evento> eventiNonIscritto = eventoRepository.findEventiUtenteNonIscritto(utenteID);
        System.out.println("#\tlista non iscritto");
        for(int i = 0; i < eventiNonIscritto.size(); i++){
            System.out.println("#\t\t" + eventiNonIscritto.get(i).getId() + ", " + eventiNonIscritto.get(i).getNome());
        }
        //stop debug
        List<Evento> eventi = eventoRepository.findEventiUtenteNonIscrittoNonScaduti(utenteID, ottieniData() );
        System.out.println("# getEventiUtenteNonIscritto: size: " + eventi.size());
        return eventi;
    }

    @GetMapping("/nome/{nome}")
    public List<Evento> trovaPerNome(@PathVariable String nome){
        List <Evento> eventi = eventoRepository.findByNome(nome);
        return eventi;
    }

    @GetMapping("/tipo/{tipo}")
    public List<Evento> trovaPerTipo(@PathVariable long tipo){
        System.out.println("# trovaPerTipo: tipo richiesto: " + tipo);
        List<Evento> eventi = eventoRepository.findByTipoEventoId(tipo);
        System.out.println("# trovaPerTipo: elementi trovati: " + eventi.size());
        return eventi;
    }

    //si può cancellare
    @GetMapping("/info-evento/{id}")
    public Evento trovaPerID(@PathVariable long id){
        System.out.println("# trovaPerID: id = " + id);
        Optional<Evento> evento = eventoRepository.findById(id);
        if(evento.isPresent()){
            System.out.println("#\ttrovaPerID: esiste evento " + id);
            return evento.get();
        }else{
            System.out.println("#\ttrovaPerID: NON esiste evento " + id);
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
        System.out.println("# richiesta di tutti gli eventi del comune: " + comune);
        List<Evento> eventi = eventoRepository.findByComune(comune);
        return eventi;
    }

    @GetMapping("/eventi-comune-non-scaduti/{comune}")
    public List<Evento> eventiDelComuneNonScaduti(@PathVariable long comune){
        System.out.println("# richiesta di tutti gli eventi del comune: " + comune);
        List<Evento> eventi = eventoRepository.findByComuneNonScaduti(comune, ottieniData());
        return eventi;
    }

    @GetMapping("/iscrizione-utente/{utenteID}")
    public List<Evento> eventiUtenteIscritto(@PathVariable long utenteID){
        //restituisce l'elenco di tutti gli eventi a cui è iscritto un utente
        return eventoRepository.findIscrizioniUtente(utenteID);
    }

    @GetMapping("/iscrizione-utente-non-scaduti/{utenteID}")
    public List<Evento> eventiUtenteIscrittoNonScaduti(@PathVariable long utenteID){
        //restituisce l'elenco di tutti gli eventi a cui è iscritto un utente
        return eventoRepository.findIscrizioniUtenteNonScaduti(utenteID, ottieniData());
    }

    @PostMapping("/disiscrivi")
    public ResponseEntity<Map<String,String>> disiscrivi(@RequestBody Map<String,Long> body){
        Evento evento = trovaPerID(body.get("evento_id"));
        long utenteID = body.get("utente_id");
        Map<String,String> response = new HashMap<>();
        if(evento.getIscritti().contains(utenteID)){
            evento.getIscritti().remove(utenteID);
            evento.setPartecipanti(evento.getPartecipanti() - 1);
            eventoRepository.save(evento);
            response.put("messaggio", "Utente <" + utenteID + "> disiscritto dall'evento " + evento.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.put("messaggio", "Utente <" + utenteID + "> non risulta iscritto all evento:  " + evento.getId());
            return new ResponseEntity<>(response,  HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> rimuoviTuttiEventi(){
        eventoRepository.deleteAll();
        return new ResponseEntity<>("tutti gli eventi sono stati cancellati con successo", HttpStatus.OK);
    }

    @DeleteMapping("/deleteById/{eventoID}")
    public ResponseEntity<Map<String, String>> eliminaEventoID(@PathVariable long eventoID/*@PathVariable long  id*/){
        Map<String, String> response = new HashMap<String, String>();
        if(trovaPerID(eventoID) != null){
            eventoRepository.deleteById(eventoID);
            response.put("messaggio", "Eliminazione avvenuta con successo");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.put("messaggio", "Non esiste l'evento che si desidera eliminare");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public Evento addEvento(@RequestBody Evento evento){
        //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        System.out.println("# Aggiungi evento");
        System.out.println("#\taddEvento: aggiungo evento: " + evento);
        System.out.println("#\taddEvento: tipo evento: " + evento.getTipoEvento().getId() + " = " + evento.getTipoEvento().getNome());
        System.out.println("#\taddEvento: data: " + evento.getData());
        Evento nuovoEvento = eventoRepository.save(new Evento(evento.getNome(), evento.getNumMaxPartecipanti(),
                evento.getPartecipanti(), evento.isStreaming(), evento.getDescrizione(), evento.getNote(),
                evento.getTipoEvento(), evento.getData(), evento.getProprietario(), evento.getComune(), evento.getIndirizzo(),
                evento.getPrezzo(), evento.getCoordinate()));
        return nuovoEvento;
    }

    @PostMapping("/iscrivi")
    public ResponseEntity<Evento> iscrivi(@RequestBody IscriviEvento iscriviEvento){
        System.out.println("# iscrivi: Ho ricevuto: evento = " + iscriviEvento.getEvento().getId() + "; utente = " + iscriviEvento.getUtente());
        iscriviEvento.getEvento().getIscritti().add(iscriviEvento.getUtente());
        return new ResponseEntity<>(eventoRepository.save(iscriviEvento.getEvento()), HttpStatus.OK);
        //return new ResponseEntity<>("Utente <" + iscriviEvento.getUtente() + "> iscritto all'evento " + iscriviEvento.getEvento().getId(), HttpStatus.OK);
    }

    //TODO: verificare se si può creare in maniera più "bella"
    @PutMapping("/aggiorna/{id}")
    public ResponseEntity<Evento> aggiornaEvento(@PathVariable("id") long id, @RequestBody Evento evento){
        System.out.println("# aggiornaEvento: ricevuto id = " + id);
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
            _evento.setComune(evento.getComune());
            _evento.setIndirizzo(evento.getIndirizzo());
            _evento.setPrezzo(evento.getPrezzo());
            _evento.setCoordinate(evento.getCoordinate());
            return new ResponseEntity<>(eventoRepository.save(_evento), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/prenota")
    public ResponseEntity<Map<String, String>> prenotaEvento(@RequestBody Map<String, Long> body){
        //verifico che ci siano ancora posti disponibili
        long eventoID = body.get("evento_id");
        long utenteID = body.get("utente_id");
        System.out.println("# richiesta iscrizione da: <" + utenteID + "> per <" + eventoID + ">");
        Evento evento = trovaPerID(eventoID);
        String message;
        Map<String,String> response = new HashMap<>();
        if(evento.getPartecipanti() >= evento.getNumMaxPartecipanti()){
            System.out.println("#\trichiesta iscrizione: posti esauriti");
            message = "Non ci sono più posti disponibili";
            response.put("messaggio", message);
            return new ResponseEntity<Map<String,String>>(response, HttpStatus.CONFLICT);
        }
        //controllo che non si sia già iscritto
        boolean daIscrivere = eventoRepository.findOccorrenzeIscrizioniUtenteStessoEvento(utenteID, eventoID) == 0;
        if(! daIscrivere){
            System.out.println("#\trichiesta iscrizione: già iscritto");
            message = "Risulti già iscritto a questo evento!";
            response.put("messaggio", message);
            return new ResponseEntity<Map<String,String>>(response, HttpStatus.CONFLICT);
        }
        //aggiungo l'iscrizione e incremento di 1 il valore degli iscritti
        evento.setPartecipanti(evento.getPartecipanti() + 1);
        evento.getIscritti().add(utenteID);
        aggiornaEvento(eventoID, evento);
        System.out.println("#\trichiesta iscrizione: iscrizione avvenuta");
        message = "Iscrizione completata!";
        response.put("messaggio", message);
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.OK);
    }

    private Date ottieniData(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        System.out.println("#\tdata: " + date);
        return date;
    }

}
