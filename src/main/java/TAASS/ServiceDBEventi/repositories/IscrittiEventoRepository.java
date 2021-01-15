package TAASS.ServiceDBEventi.repositories;

import TAASS.ServiceDBEventi.models.Evento;
import TAASS.ServiceDBEventi.models.IscrittiEvento;
import org.hibernate.annotations.SQLInsert;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;
import java.util.List;

@Repository
public interface IscrittiEventoRepository extends JpaRepository<IscrittiEvento, Long> {
    List<IscrittiEvento> findByUtenteID(long utenteID);

    @Modifying
    @Query("delete from IscrittiEvento i where i.utenteID = :utente and i.evento = :evento")
    void deleteRegistrazione(@Param("utente") Long utente, @Param("evento") Long evento);

    List<IscrittiEvento> findByEvento(long evento);

    /*@Modifying
    @Transactional
    @SQLInsert(sql = "insert into  iscritti_evento (utente_id, evento_id) values(?, ?)")
    IscrittiEvento registraIscrizione(@Param("utente_id") long utente_id, @Param("evento_id") long evento_id);*/

    //@SQLInsert(sql = "INSERT IGNORE INTO users(first_name, last_name, email) " +
    //        "VALUES (?, ?, ?)" )
    /*@Modifying
    @SQLInsert(sql = "insert into  iscritti_evento (utente_id, evento_id) values(:utente_id, :evento_id)")
    @Transactional
    IscrittiEvento registraIscrizione(@Param("utente_id") long utenteID, @Param("evento_id") long id);*/

    /*@Modifying
    @Query("insert into IscrittiEvento (utente_id, evento_id) values :utente_id,:evento_id")
    public IscrittiEvento registraIscrizione(@Param("utente_id")long utente_id, @Param("evento_id")long evento_id);*/

}
