package TAASS.ServiceDBEventi.repositories;

import TAASS.ServiceDBEventi.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByNome(String nome);

    List<Evento> findByComune(long comune);

    List<Evento> findByTipoEventoId(long tipo);

    //il ?1 indica il primo parametro
    @Query(value = "SELECT evento.* FROM evento where evento.id NOT IN (SELECT evento_iscritti.evento_id FROM evento_iscritti where evento_iscritti.iscritti = ?1) ORDER BY evento.data  DESC", nativeQuery = true)
    List<Evento> findEventiUtenteNonIscritto(long utenteID);

    @Query(value = "SELECT evento.* FROM evento where evento.id NOT IN (SELECT evento_iscritti.evento_id FROM evento_iscritti where evento_iscritti.iscritti = ?1) and evento.data >= ?2 ORDER BY evento.data  DESC", nativeQuery = true)
    List<Evento> findEventiUtenteNonIscrittoNonScaduti(long utenteID, Date date);

    //questa query restituisce quante volte risulto essere iscritto all'evento (questo serve per verificare se uno è già iscritto o no)
    @Query(value = "select count(evento_iscritti.evento_id) from evento_iscritti where evento_iscritti.iscritti = ?1 and evento_iscritti.evento_id = ?2", nativeQuery = true)
    int findOccorrenzeIscrizioniUtenteStessoEvento(long utenteID, long eventoID);

    @Query(value = "select evento.* from evento where evento.id in (select evento_iscritti.evento_id from evento_iscritti where evento_iscritti.iscritti = ?1) ORDER BY evento.data DESC", nativeQuery = true)
    List<Evento> findIscrizioniUtente(long utenteID);

    @Query(value = "select evento.* from evento where evento.id in (select evento_iscritti.evento_id from evento_iscritti where evento_iscritti.iscritti = ?1) and evento.data >= ?2 ORDER BY evento.data DESC", nativeQuery = true)
    List<Evento> findIscrizioniUtenteNonScaduti(long utenteID, Date ottieniData);

    @Query(value = "select evento.* from  evento where evento.data >= ?1 ORDER BY evento.data DESC", nativeQuery = true)
    List<Evento> findTuttiEventiNonScaduti(Date date);

    @Query(value = "select evento.* from evento where evento.comune = ?1 and evento.data >= ?2 ORDER BY evento.data DESC", nativeQuery = true)
    List<Evento> findByComuneNonScaduti(long comune, Date ottieniData);

}
