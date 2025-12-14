package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.analyzer.model.Sensor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    @Query("""
        SELECT s FROM Sensor s
        LEFT JOIN FETCH s.scenarioConditions sc
        LEFT JOIN FETCH s.scenarioActions sa
        WHERE s.id = :id AND s.hubId = :hubId
        """)
    Optional<Sensor> findByIdAndHubId(@Param("id") String id, @Param("hubId") String hubId);

    @Query("""
        SELECT s FROM Sensor s
        LEFT JOIN FETCH s.scenarioConditions sc
        LEFT JOIN FETCH sc.condition
        LEFT JOIN FETCH sc.scenario
        LEFT JOIN FETCH s.scenarioActions sa
        LEFT JOIN FETCH sa.action
        LEFT JOIN FETCH sa.scenario
        WHERE s.hubId = :hubId
        """)
    List<Sensor> findByHubIdWithScenarios(@Param("hubId") String hubId);
}
