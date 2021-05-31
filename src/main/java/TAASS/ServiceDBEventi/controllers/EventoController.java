package TAASS.ServiceDBEventi.controllers;

import TAASS.ServiceDBEventi.classiComode.IscriviEvento;
import TAASS.ServiceDBEventi.exception.MyCustomException;
import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/evento")
public class EventoController {
    //x-auth-user-role  ROLE_ADMIN, ROLE_CLIENT, ROLE_MAYOR, ROLE_PUBLISHER

    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public List<Evento> getAllEventi(HttpServletRequest requestHeader){
        /*System.out.println("**************************************************************************************************************");

        Enumeration headerNames = requestHeader.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = requestHeader.getHeader(key);
            System.out.println("HEADER: " + key +" " + value);
        }*/


        /*if(requestHeader.getHeader("x-auth-user-role").equals("ROLE_ADMIN")){
            System.out.println("Sei autorizzato ad accedere a questo metodo");
        }else{
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }*/
        //Auth: solo admin
        if(!requestHeader.getHeader("x-auth-user-role").equals("ROLE_ADMIN")){
            //Solo un admin può guardare tutti gli eventi di tutti, anche quelli scaduti
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }

        List<Evento> eventi = new ArrayList<>();
        eventoRepository.findAll().forEach(eventi::add);
        return eventi;
    }

    @GetMapping("/non-scaduti")
    public List<Evento> getEventiNonScaduti(HttpServletRequest requestHeader){
        //Auth: all
        List<Evento> eventi;
        Date date =ottieniData();
        System.out.println("# eventi non scaduti: data attuale: " + date);
        eventi = eventoRepository.findTuttiEventiNonScaduti(date);
        return eventi;
    }

    @GetMapping("/eventi-non-iscritto/{utenteID}")
    public List<Evento> getEventiUtenteNonIscritto(@PathVariable long utenteID ){
        //TODO: SERVE?
        System.out.println("# getEventiUtenteNonIscritto: uid: " + utenteID);
        List<Evento> eventi = eventoRepository.findEventiUtenteNonIscritto(utenteID);
        System.out.println("# getEventiUtenteNonIscritto: size: " + eventi.size());
        return eventi;
    }

    @GetMapping("/eventi-non-iscritto-non-scaduti/{utenteID}")
    public List<Evento> getEventiUtenteNonIscrittoNonscaduti(HttpServletRequest requestHeader, @PathVariable long utenteID ){
        //Auth: solo l'utente con id = utenteID
        if(Integer.parseInt(requestHeader.getHeader("x-auth-user-id")) != utenteID){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        System.out.println("# getEventiUtenteNonIscritto: uid: " + utenteID);
        //start debug
        //stampa tutti gli iscritti a tutti gli eventi
        List<Evento> tuttiEventi = getAllEventi(requestHeader);
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
        //Auth: tutti
        List <Evento> eventi = eventoRepository.findByNome(nome);
        return eventi;
    }

    @GetMapping("/tipo/{tipo}")
    public List<Evento> trovaPerTipo(@PathVariable long tipo){
        //Auth: tutti
        System.out.println("# trovaPerTipo: tipo richiesto: " + tipo);
        List<Evento> eventi = eventoRepository.findByTipoEventoId(tipo);
        System.out.println("# trovaPerTipo: elementi trovati: " + eventi.size());
        return eventi;
    }

    @GetMapping("/info-evento/{id}")
    public Evento trovaPerID(@PathVariable long id){
        //Auth: tutti
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
    public HashSet<Long> iscrittiEvento(@PathVariable long id,HttpServletRequest requestHeader){
        //Auth: Admin, sindaco del comune dell'evento e pubblicatore dell'evento
        switch (requestHeader.getHeader("x-auth-user-role")){
            case "ROLE_ADMIN":{
                //può accedere
                break;
            }
            case "ROLE_CLIENT":{
                //non può accedere a questo
                throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
            case "ROLE_MAYOR":{
                //può accedere solo se l'evento è del comune del sindaco
                break;
            }
            case "ROLE_PUBLISHER":{
                //può accedere solo se è il publisher dello stesso evento
                break;
            }
            default:{
                throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
        }
        Optional<Evento> iscritti = eventoRepository.findById(id);
        if(iscritti.isPresent()){
            return (HashSet<Long>) iscritti.get().getIscritti();
        }else{
            return null;
        }

    }

    @GetMapping("/eventi-comune/{comune}")
    public List<Evento> eventiDelComune(@PathVariable long comune){
        //permette di vedere anche gli eventi scaduti
        //Auth: sindaco e anche publisher

        System.out.println("# richiesta di tutti gli eventi del comune: " + comune);
        List<Evento> eventi = eventoRepository.findByComune(comune);
        return eventi;
    }

    @GetMapping("/eventi-comune-non-scaduti/{comune}")
    public List<Evento> eventiDelComuneNonScaduti(@PathVariable long comune){
        //Auth: tutti
        System.out.println("# richiesta di tutti gli eventi del comune: " + comune);
        List<Evento> eventi = eventoRepository.findByComuneNonScaduti(comune, ottieniData());
        return eventi;
    }

    @GetMapping("/iscrizione-utente/{utenteID}")
    public List<Evento> eventiUtenteIscritto(@PathVariable long utenteID, HttpServletRequest requestHeader){
        //Auth: utente con id = utenteID
        //restituisce l'elenco di tutti gli eventi a cui è iscritto un utente
        if(Integer.parseInt(requestHeader.getHeader("x-auth-user-id")) != utenteID){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        return eventoRepository.findIscrizioniUtente(utenteID);
    }

    @GetMapping("/iscrizione-utente-non-scaduti/{utenteID}")
    public List<Evento> eventiUtenteIscrittoNonScaduti(@PathVariable long utenteID, HttpServletRequest requestHeader){
        //Auth: utente con id = utenteID
        //restituisce l'elenco di tutti gli eventi a cui è iscritto un utente
        if(Integer.parseInt(requestHeader.getHeader("x-auth-user-id")) != utenteID){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        return eventoRepository.findIscrizioniUtenteNonScaduti(utenteID, ottieniData());
    }

    @PostMapping("/disiscrivi")
    public ResponseEntity<Map<String,String>> disiscrivi(@RequestBody Map<String,Long> body, HttpServletRequest requestHeader){
        //Auth: utente con id = utenteID
        Evento evento = trovaPerID(body.get("evento_id"));
        long utenteID = body.get("utente_id");
        if(Integer.parseInt(requestHeader.getHeader("x-auth-user-id")) != utenteID){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
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
    public ResponseEntity<String> rimuoviTuttiEventi(HttpServletRequest requestHeader){
        //Auth: admin
        if(!requestHeader.getHeader("x-auth-user-role").equals("ROLE_ADMIN")){
            //Solo un admin può guardare tutti gli eventi di tutti, anche quelli scaduti
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        eventoRepository.deleteAll();
        return new ResponseEntity<>("tutti gli eventi sono stati cancellati con successo", HttpStatus.OK);
    }

    @DeleteMapping("/deleteById/{eventoID}")
    public ResponseEntity<Map<String, String>> eliminaEventoID(@PathVariable long eventoID/*@PathVariable long  id*/){
        //Admin: sindaco del comune dell'evento e pubblicatore dell'evento
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
        //Auth: admin, sindaco e publisher del comune
        //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        System.out.println("# Aggiungi evento");
        System.out.println("#\taddEvento: aggiungo evento: " + evento);
        System.out.println("#\taddEvento: tipo evento: " + evento.getTipoEvento().getId() + " = " + evento.getTipoEvento().getNome());
        System.out.println("#\taddEvento: data: " + evento.getData());
        Evento nuovoEvento = eventoRepository.save(new Evento(evento.getNome(), evento.getNumMaxPartecipanti(),
                evento.getPartecipanti(), evento.isStreaming(), evento.getDescrizione(), evento.getNote(),
                evento.getTipoEvento(), evento.getData(), evento.getProprietario(), evento.getComune(), evento.getIndirizzo(),
                evento.getPrezzo(), evento.getLatitudine(), evento.getLongitudine()));
        return nuovoEvento;
    }

    @PostMapping("/iscrivi")
    public ResponseEntity<Evento> iscrivi(@RequestBody IscriviEvento iscriviEvento){
        //Auth: utente con id = utenteid
        System.out.println("# iscrivi: Ho ricevuto: evento = " + iscriviEvento.getEvento() + "; utente = " + iscriviEvento.getUtente());
        Optional<Evento> evento = eventoRepository.findById(iscriviEvento.getEvento());
        try {
            evento.get().getIscritti().add(iscriviEvento.getUtente());
        }catch (Exception exception){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(eventoRepository.save(evento.get()), HttpStatus.OK);
        //return new ResponseEntity<>("Utente <" + iscriviEvento.getUtente() + "> iscritto all'evento " + iscriviEvento.getEvento().getId(), HttpStatus.OK);
    }

    //TODO: verificare se si può creare in maniera più "bella"
    @PutMapping("/aggiorna/{id}")
    public ResponseEntity<Evento> aggiornaEvento(@PathVariable("id") long id, @RequestBody Evento evento){
        //Auth: admin, sindaco del comune, publisher proprietario
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
            _evento.setLatitudine(evento.getLatitudine());
            _evento.setLongitudine(evento.getLongitudine());
            return new ResponseEntity<>(eventoRepository.save(_evento), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //TODO: cancellare
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
