package TAASS.ServiceDBEventi.repositories;

import TAASS.ServiceDBEventi.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByNome(String nome);

    List<Evento> findByComune(long comune);

    List<Evento> findByTipoEventoId(long tipo);

}
