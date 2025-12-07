package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scenarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hub_id", "name"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "hub_id", nullable = false)
    private String hubId;
    private String name;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScenarioCondition> scenarioConditions = new HashSet<>();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ScenarioAction> scenarioActions = new HashSet<>();

    public void addCondition(ScenarioCondition scenarioCondition) {
        this.scenarioConditions.add(scenarioCondition);
    }

    public void addAction(ScenarioAction action) {
        this.scenarioActions.add(action);
    }
}
