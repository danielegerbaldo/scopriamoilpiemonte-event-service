package TAASS.ServiceDBEventi.rabbitMQ.DTO;


import java.util.List;

public class Utente {

    private Long id;

    private String nome;

    private String cognome;

    private String cf;

    private String telefono;

    private long comuneResidenza;   //comune di residenza

    private String email;

    private String password;

    List<Role> ruoli;

    private long dipendenteDiComune;    //comune del quale sono sindaco o pubblicatore

    public Utente(Long id, String nome, String cognome, String cf, String telefono, long comuneResidenza, String email, String password, List<Role> ruolo, long dipendenteDiComune) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.telefono = telefono;
        this.comuneResidenza = comuneResidenza;
        this.email = email;
        this.password = password;
        this.ruoli = ruolo;
        this.dipendenteDiComune = dipendenteDiComune;
    }

    public Utente() {
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

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public long getComuneResidenza() {
        return comuneResidenza;
    }

    public void setComuneResidenza(long comuneResidenza) {
        this.comuneResidenza = comuneResidenza;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRuoli() {
        return ruoli;
    }

    public void setRuoli(List<Role> ruolo) {
        this.ruoli = ruolo;
    }

    public long getDipendenteDiComune() {
        return dipendenteDiComune;
    }

    public void setDipendenteDiComune(long comune) {
        this.dipendenteDiComune = comune;
    }
}
