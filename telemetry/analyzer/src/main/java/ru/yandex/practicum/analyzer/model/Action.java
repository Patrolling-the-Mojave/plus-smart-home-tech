package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActionType type;
    private Integer value;
    @OneToMany(mappedBy = "action", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<ScenarioAction> scenarioActions = new ArrayList<>();
}
