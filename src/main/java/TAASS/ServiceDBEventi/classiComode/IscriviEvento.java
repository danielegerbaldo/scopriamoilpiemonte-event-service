package TAASS.ServiceDBEventi.classiComode;

import TAASS.ServiceDBEventi.models.Evento;

public class IscriviEvento {

    /*
    NOTE IMPORTANTI:
    Non potendo passare due @requestbody alle richieste si possono creare delle classi comode che contengono queste
    due informazioni insieme
     */

    private long evento;
    private long utente;

    public IscriviEvento(long evento, long utente) {
        this.evento = evento;
        this.utente = utente;
    }

    public long getEvento() {
        return evento;
    }

    public void setEvento(long evento) {
        this.evento = evento;
    }

    public long  getUtente() {
        return utente;
    }

    public void setUtente(long utente) {
        this.utente = utente;
    }
}
