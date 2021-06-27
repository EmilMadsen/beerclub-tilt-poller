package dk.thebeerclub.brewhub.repository;

import dk.thebeerclub.brewhub.model.TiltLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TiltLogRepository extends JpaRepository<TiltLog, Long> {
    Optional<TiltLog> findTop1ByParentIdOrderByTimestampDesc(Long parentId);
}
