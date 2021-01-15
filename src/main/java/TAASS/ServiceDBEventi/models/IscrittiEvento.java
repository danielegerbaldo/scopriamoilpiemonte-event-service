package TAASS.ServiceDBEventi.models;

import javax.persistence.*;
import java.io.Serializable;

class IDClass implements Serializable {
    long utenteID = 0;
    Evento evento = null;

    public IDClass(long utenteID, Evento evento) {
        this.utenteID = utenteID;
        this.evento = evento;
    }

    public IDClass() {
    }

    public long getUtenteID() {
        return utenteID;
    }

    public void setUtenteID(long utenteID) {
        this.utenteID = utenteID;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}

@Entity
@IdClass(IDClass.class)
public class IscrittiEvento{
    @Id
    @Column(name = "utente_id")
    long utenteID;

    @Id
    @JoinColumn(name = "evento_id")
    @OneToOne(cascade = CascadeType.REMOVE)     //mettto Remove perché quando un utente si disiscrive rimuovo tutte le sue prenotazioni
    Evento evento;

    public IscrittiEvento(long utenteID, Evento evento) {
        this.utenteID = utenteID;
        this.evento = evento;
    }

    public IscrittiEvento() {
    }

    public long getUtenteID() {
        return utenteID;
    }

    public void setUtenteID(long utenteID) {
        this.utenteID = utenteID;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}