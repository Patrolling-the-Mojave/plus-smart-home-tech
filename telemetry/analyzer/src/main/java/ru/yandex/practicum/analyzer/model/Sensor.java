package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sensors")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    private String id;
    @Column(name = "hub_id", nullable = false)
    private String hubId;
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScenarioCondition> scenarioConditions = new HashSet<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScenarioAction> scenarioActions = new HashSet<>();
}
