package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compilation_seq")
    @SequenceGenerator(name = "compilation_seq", initialValue = 0, allocationSize = 1)
    private Long id;
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;
    private boolean pinned;
    private String title;
}
