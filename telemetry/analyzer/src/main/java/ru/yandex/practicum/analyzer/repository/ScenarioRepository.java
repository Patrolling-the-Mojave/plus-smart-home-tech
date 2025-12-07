package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.analyzer.model.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    @Query("SELECT DISTINCT s FROM Scenario s " +
            "LEFT JOIN s.scenarioConditions sc " +
            "LEFT JOIN s.scenarioActions sa " +
            "WHERE sc.sensor.id = :sensorId OR sa.sensor.id = :sensorId")
    List<Scenario> findBySensorId(@Param("sensorId") String sensorId);
}
