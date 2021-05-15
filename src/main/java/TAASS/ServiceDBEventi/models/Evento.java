package TAASS.ServiceDBEventi.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "evento")
public class Evento {

    public Evento() {
    }

    public Evento(String nome, int numMaxPartecipanti, int partecipanti, boolean streaming, String descrizione,
                  String note, TipoEvento tipoEvento, Date data, long proprietario, long comune, String indirizzo,
                  double prezzo, double latitudine, double longitudine) {
        this.nome = nome;
        this.numMaxPartecipanti = numMaxPartecipanti;
        this.partecipanti = partecipanti;
        this.streaming = streaming;
        this.descrizione = descrizione;
        this.note = note;
        this.tipoEvento = tipoEvento;
        this.data = data;
        this.proprietario = proprietario;
        this.comune = comune;
        this.indirizzo = indirizzo;
        this.prezzo = prezzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        iscritti = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="nome")
    private String nome;

    @Column(name="num_max_partecipanti")
    private int numMaxPartecipanti;

    @Column(name="partecipanti")
    private int partecipanti;

    @Column(name="streaming")
    private boolean streaming;

    @Column(name="descrizione")
    private String descrizione;

    @Column(name="note")
    private String note;

    //@Column(name="tipo_evento")
    @ManyToOne(cascade = {CascadeType.ALL})
    private TipoEvento tipoEvento;

    @Column(name="data")
    private Date data;

    @Column(name="proprietario")
    private long proprietario;

    @Column(name = "comune")
    private long comune;

    @Column(name = "indirizzo")
    private String indirizzo;

    @ElementCollection
    private Set<Long> iscritti;

    @Column(name = "prezzo")
    private double prezzo;

    @Column(name = "latitudine")
    private double latitudine;

    @Column(name = "longitudine")
    private double longitudine;




    // getter e setter
    public Set<Long> getIscritti() {
        return iscritti;
    }

    public void setIscritti(Set<Long> iscritti) {
        this.iscritti = iscritti;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNumMaxPartecipanti() {
        return numMaxPartecipanti;
    }

    public void setNumMaxPartecipanti(int numMaxPartecipanti) {
        this.numMaxPartecipanti = numMaxPartecipanti;
    }

    public int getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(int partecipanti) {
        this.partecipanti = partecipanti;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public long getProprietario() {
        return proprietario;
    }

    public void setProprietario(long proprietario) {
        this.proprietario = proprietario;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public void setLatitudine(double latitude) {
        this.latitudine = latitude;
    }

    public void setLongitudine(double longitude) {
        this.longitudine = longitude;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public Long castLongObject(){
        return id;
    }

    public long getComune() {
        return comune;
    }

    public void setComune(long comune) {
        this.comune = comune;
    }
}
