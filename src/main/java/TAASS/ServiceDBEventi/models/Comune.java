package TAASS.ServiceDBEventi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "comune")
public class Comune {
    public Comune() {
    }

    public Comune(Long id, String nome, String CAP, String provincia) {
        this.id = id;
        this.nome = nome;
        this.CAP = CAP;
        this.provincia = provincia;
    }

    public String generaIndirizzo(String via){
        return "" + via + "; " + this.nome + "(" + this.provincia + ")" + ", " + this.CAP;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="nome")
    private String nome;

    @Column(name="CAP")
    private String CAP;

    @Column(name = "provincia")
    private String provincia;

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

    public String getCAP() {
        return CAP;
    }

    public void setCAP(String CAP) {
        this.CAP = CAP;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
}
