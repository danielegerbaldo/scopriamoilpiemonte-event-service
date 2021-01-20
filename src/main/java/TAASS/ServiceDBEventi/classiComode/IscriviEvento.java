package TAASS.ServiceDBEventi.classiComode;

import TAASS.ServiceDBEventi.models.Evento;

public class IscriviEvento {

    /*
    NOTE IMPORTANTI:
    Non potendo passare due @requestbody alle richieste si possono creare delle classi comode che contengono queste
    due informazioni insieme
     */

    private Evento evento;
    private long utente;

    public IscriviEvento(Evento evento, long utente) {
        this.evento = evento;
        this.utente = utente;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public long  getUtente() {
        return utente;
    }

    public void setUtente(long utente) {
        this.utente = utente;
    }
}
