package TAASS.ServiceDBEventi.controllers;

import TAASS.ServiceDBEventi.classiComode.IscriviEvento;
import TAASS.ServiceDBEventi.exception.MyCustomException;
import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.models.TipoEvento;
import TAASS.ServiceDBEventi.rabbitMQ.ListenerService;
import TAASS.ServiceDBEventi.rabbitMQ.PublishService;
import TAASS.ServiceDBEventi.repositories.EventoRepository;
import TAASS.ServiceDBEventi.repositories.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/v1/evento")
public class EventoController {
    //x-auth-user-role  ROLE_ADMIN, ROLE_CLIENT, ROLE_MAYOR, ROLE_PUBLISHER

    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private TipoEventoRepository tipoEventoRepository;
    @Autowired
    private ListenerService listenerService;
    @Autowired
    private PublishService publishService;


    @GetMapping
    public List<Evento> getAllEventi(HttpServletRequest requestHeader){

        //AUTH: admin
        if(!(requestHeader.getHeader("X-auth-user-role").equals("ROLE_ADMIN"))){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        //List<Evento> eventi = new ArrayList<>();
        return eventoRepository.findAll();
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
            System.out.println("utenteID = " + utenteID + "; x-auth-user-id = " + Integer.parseInt(requestHeader.getHeader("x-auth-user-id")));
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        System.out.println("# getEventiUtenteNonIscritto: uid: " + utenteID);
        //start debug
        //stampa tutti gli iscritti a tutti gli eventi
        /*List<Evento> tuttiEventi = getAllEventi(requestHeader);
        System.out.println("#\tlista iscrizioni eventi");
        for(int i = 0; i < tuttiEventi.size(); i++){
            System.out.println("#\t\tevento: " + tuttiEventi.get(i).getId() + ", nome = " + tuttiEventi.get(i).getNome());
            ArrayList<Long> iscritti = new ArrayList<Long> (tuttiEventi.get(i).getIscritti()) ;
            for(int j = 0; j < iscritti.size(); j++ ){
                System.out.println("#\t\t\t-" +  iscritti.get(j));
            }
        }*/
        //stampa tutti gli eventi a cui non sono iscritto
        /*List<Evento> eventiNonIscritto = eventoRepository.findEventiUtenteNonIscritto(utenteID);
        System.out.println("#\tlista non iscritto");
        for(int i = 0; i < eventiNonIscritto.size(); i++){
            System.out.println("#\t\t" + eventiNonIscritto.get(i).getId() + ", " + eventiNonIscritto.get(i).getNome());
        }*/
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
    public Evento trovaPerID(@PathVariable long id, HttpServletRequest requestHeader){
        //Auth: tutti
        System.out.println("# trovaPerID: id = " + id);
        Optional<Evento> evento = eventoRepository.findById(id);
        if(evento.isPresent()){
            System.out.println("#\ttrovaPerID: esiste evento " + id);
            //verificare l'autorizzazione dell'utente
            String auth = requestHeader.getHeader("x-auth-user-role");
            long utenteID = Integer.parseInt(requestHeader.getHeader("x-auth-user-id"));
            long idComuneDipendente = -1;
            if(requestHeader.getHeader("X-auth-user-comune-dipendente-id") != null){
                idComuneDipendente = Long.parseLong(requestHeader.getHeader("X-auth-user-comune-dipendente-id"));
            }

            if(auth.equals("ROLE_CLIENT")
                    || (auth.equals("ROLE_PUBLISHER")&& evento.get().getProprietario() != utenteID )
                    || (auth.equals("ROLE_MAYOR") && evento.get().getComune() != idComuneDipendente) ){
                //se l'utente loggato è un cliente o non è sindaco del comune dell'evento o non è proprietario dell'evento
                //non deve poter vedere l'elenco degli ID degli utenti iscritti al comune
                evento.get().setIscritti(new HashSet<>());
            }
            return evento.get();
        }else{
            System.out.println("#\ttrovaPerID: NON esiste evento " + id);
            return null;
        }

    }

    @GetMapping("/iscritti-evento/{id}")
    public HashSet<Long> iscrittiEvento(@PathVariable long id,HttpServletRequest requestHeader){
        //Auth: Admin, sindaco del comune dell'evento e pubblicatore dell'evento
        Optional<Evento> iscritti = eventoRepository.findById(id);
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
                long idComuneDipendente = Long.parseLong(requestHeader.getHeader("X-auth-user-comune-dipendente-id"));
                if(idComuneDipendente != iscritti.get().getComune()){
                    throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
                }
                break;
            }
            case "ROLE_PUBLISHER":{
                //può accedere solo se è il publisher dello stesso evento
                long idProprietario = Long.parseLong(requestHeader.getHeader("X-auth-user-id"));
                if(idProprietario != iscritti.get().getProprietario()){
                    throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
                }
                break;
            }
            default:{
                throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
        }

        if(iscritti.isPresent()){
            return (HashSet<Long>) iscritti.get().getIscritti();
        }else{
            return null;
        }

    }

    @GetMapping("/eventi-comune/{comune}")
    public List<Evento> eventiDelComune(@PathVariable long comune, HttpServletRequest requestHeader){
        //permette di vedere anche gli eventi scaduti
        //Auth: sindaco e anche publisher

        switch (requestHeader.getHeader("x-auth-user-role")){
            case "ROLE_ADMIN":{
                //può accedere
                break;
            }
            case "ROLE_CLIENT":{
                //non può accedere a questo
                throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
            case "ROLE_MAYOR":  //che sia un sindaco o un pubblicatore allora controllo che lavorino nel comune indicato
            case "ROLE_PUBLISHER":{
                //può accedere solo se è il publisher dello stesso evento
                long idComuneDipendente = Long.parseLong(requestHeader.getHeader("X-auth-user-comune-dipendente-id"));
                if(idComuneDipendente != comune){
                    throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
                }
                break;
            }
            default:{
                throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
        }

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
    public ResponseEntity<Map<String,String>> disiscrivi(@RequestParam long idUtente, @RequestParam long idEvento, HttpServletRequest requestHeader){


        if(!eventoRepository.findById(idEvento).isPresent()){
            throw new MyCustomException("NOT FOUND", HttpStatus.NOT_FOUND);
        }

        Evento evento = eventoRepository.findById(idEvento).get();

        //Auth: utente con id = utenteID
        if((Integer.parseInt(requestHeader.getHeader("x-auth-user-id")) != idUtente || !requestHeader.getHeader("x-auth-user-role").equals("ROLE_ADMIN"))){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }

        Map<String,String> response = new HashMap<>();
        if(evento.getIscritti().contains(idUtente)){
            evento.getIscritti().remove(idUtente);
            evento.setPartecipanti(evento.getPartecipanti() - 1);
            eventoRepository.save(evento);

            //Send unsubscribe event on RabbitMQ queue
            publishService.publishSubscriptionUser(idUtente,evento.getId(),false);

            response.put("messaggio", "Utente <" + idUtente + "> disiscritto dall'evento " + evento.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            response.put("messaggio", "Utente <" + idUtente + "> non risulta iscritto all evento:  " + evento.getId());
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
    public ResponseEntity<String> eliminaEventoID(@PathVariable long eventoID, HttpServletRequest requestHeader){
        //Admin: sindaco del comune dell'evento e pubblicatore dell'evento
        Optional<Evento> eventoOPT = eventoRepository.findById(eventoID);
        ResponseEntity<String> response;

        if(!eventoOPT.isPresent()){
            return new ResponseEntity<>("Non esiste un evento con id = " + eventoID, HttpStatus.NOT_FOUND);
        }
        Evento evento = eventoOPT.get();
        long proprietarioID = Long.parseLong(requestHeader.getHeader("x-auth-user-id"));
        long idComuneDipendente = Long.parseLong(requestHeader.getHeader("X-auth-user-comune-dipendente-id"));

        switch (requestHeader.getHeader("x-auth-user-role")){
            case "ROLE_ADMIN":{
                //può accedere
                eventoRepository.deleteById(eventoID);
                System.out.println("Cancellato evento id = " + eventoID);

                response= new ResponseEntity<>("Evento con id = " + eventoID + " eliminato", HttpStatus.OK);
                break;
            }
            case "ROLE_CLIENT":{
                //non può accedere a questo
                throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
            case "ROLE_MAYOR":{

                if(idComuneDipendente != evento.getComune()){
                    throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
                }else{
                    eventoRepository.deleteById(eventoID);
                    System.out.println("Cancellato evento id = " + eventoID);
                    response = new ResponseEntity<>("Evento con id = " + eventoID + " eliminato", HttpStatus.OK);
                }
                break;
            }
            case "ROLE_PUBLISHER":{
                //può accedere solo se è il publisher dello stesso evento
                if(proprietarioID != evento.getProprietario()){
                    throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
                }else{
                    eventoRepository.deleteById(eventoID);
                    System.out.println("Cancellato evento id = " + eventoID);

                    response = new ResponseEntity<>("Evento con id = " + eventoID + " eliminato", HttpStatus.OK);
                }
                break;
            }
            default:{
                throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
        }
        System.out.println("Cancella evento response = " + response.toString());
        return response;
    }

    @PostMapping
    public Evento addEvento(@RequestBody Evento evento, HttpServletRequest requestHeader){
        //AUTH: admin e sindaci/publisher che lavorano nel comune indicato nell'evento
        if(requestHeader.getHeader("x-auth-user-role").equals("ROLE_CLIENT")){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        if( (requestHeader.getHeader("x-auth-user-role").equals("ROLE_MAYOR") || requestHeader.getHeader("x-auth-user-role").equals("ROLE_PUBLISHER"))
            && Long.parseLong(requestHeader.getHeader("X-auth-user-comune-dipendente-id")) != evento.getComune()){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }

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

    @PostMapping("/addTipoEvento")
    public ResponseEntity<TipoEvento> addTipoEvento(@RequestBody TipoEvento tipoEvento, HttpServletRequest requestHeader){
        //AUTH: admin e sindaci
        if(requestHeader.getHeader("x-auth-user-role").equals("ROLE_CLIENT")){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }

        if(tipoEventoRepository.findByNome(tipoEvento.getNome()).isPresent()){
            throw new MyCustomException("TIPOEVENTO ALREADY EXIST", HttpStatus.BAD_REQUEST);
        }

        TipoEvento newTipo = new TipoEvento(tipoEvento.getNome(),tipoEvento.getDescrizione());
        tipoEventoRepository.save(newTipo);

        System.out.println("#\taddTipoEvento: tipo evento added: " + newTipo.getId() + " = " + newTipo.getNome());

        return new ResponseEntity<>(newTipo,HttpStatus.OK);
    }


    @PostMapping("/iscrivi")
    public ResponseEntity<Evento> iscrivi(@RequestBody IscriviEvento iscriviEvento){
        //Auth: utente con id = utenteid
        //TODO: finiscilo
        System.out.println("# iscrivi: Ho ricevuto: evento = " + iscriviEvento.getEvento() + "; utente = " + iscriviEvento.getUtente());
        Optional<Evento> evento = eventoRepository.findById(iscriviEvento.getEvento());
        try {
            evento.get().getIscritti().add(iscriviEvento.getUtente());
            evento.get().setPartecipanti(evento.get().getPartecipanti()+1);
            //Send subscription event on RabbitMQ queue
            publishService.publishSubscriptionUser(iscriviEvento.getUtente(),evento.get().getId(),true);
        }catch (Exception exception){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(eventoRepository.save(evento.get()), HttpStatus.OK);
        //return new ResponseEntity<>("Utente <" + iscriviEvento.getUtente() + "> iscritto all'evento " + iscriviEvento.getEvento().getId(), HttpStatus.OK);
    }

    //TODO: verificare se si può creare in maniera più "bella"
    @PutMapping("/aggiorna/{id}")
    public ResponseEntity<Evento> aggiornaEvento(@PathVariable("id") long id, @RequestBody Evento evento, HttpServletRequest requestHeader){
        //Auth: admin, sindaco del comune, publisher proprietario
        if(requestHeader.getHeader("x-auth-user-role").equals("ROLE_CLIENT")){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        if( (requestHeader.getHeader("x-auth-user-role").equals("ROLE_MAYOR"))
                && Long.parseLong(requestHeader.getHeader("X-auth-user-comune-dipendente-id")) != evento.getComune()){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        if(requestHeader.getHeader("x-auth-user-role").equals("ROLE_PUBLISHER") &&
                Long.parseLong(requestHeader.getHeader("x-auth-user-id")) != evento.getProprietario()){
            throw new MyCustomException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }

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


    private Date ottieniData(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        System.out.println("#\tdata: " + date);
        return date;
    }


}
