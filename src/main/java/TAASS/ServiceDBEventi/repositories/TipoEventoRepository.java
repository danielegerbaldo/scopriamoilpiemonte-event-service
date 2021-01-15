package TAASS.ServiceDBEventi.repositories;


import TAASS.ServiceDBEventi.models.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEvento, Long> {
}
