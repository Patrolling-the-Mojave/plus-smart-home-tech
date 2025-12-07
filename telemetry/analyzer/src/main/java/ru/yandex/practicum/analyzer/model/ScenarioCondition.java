package ru.yandex.practicum.analyzer.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Getter
@Setter
@IdClass(ScenarioConditionId.class)
@Table(name = "scenario_conditions")
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioCondition {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", nullable = false)
    private Condition condition;
}
