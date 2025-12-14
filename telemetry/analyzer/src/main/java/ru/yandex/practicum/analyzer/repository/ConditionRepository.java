package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.analyzer.model.Condition;

import java.util.Set;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    @Query("SELECT c FROM Condition c " +
            "JOIN FETCH c.scenarioConditions sc " +
            "WHERE sc.scenario.id = :scenarioId")
    Set<Condition> findByScenarioId(@Param("scenarioId") Long scenarioId);
}
