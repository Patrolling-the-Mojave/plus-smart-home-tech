package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.analyzer.model.Action;

import java.util.Set;

public interface ActionRepository extends JpaRepository<Action, Long> {
    @Query("SELECT a FROM Action a " +
            "JOIN FETCH a.scenarioActions sa " +
            "WHERE sa.scenario.id = :scenarioId")
    Set<Action> findByScenarioId(@Param("scenarioId") Long scenarioId);}
