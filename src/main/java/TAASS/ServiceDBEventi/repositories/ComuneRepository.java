package TAASS.ServiceDBEventi.repositories;

import TAASS.ServiceDBEventi.models.Comune;
import TAASS.ServiceDBEventi.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComuneRepository extends JpaRepository<Comune, Long> {
}
