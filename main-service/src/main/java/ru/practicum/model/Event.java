package ru.practicum.model;

import jakarta.persistence.*;
import lombok.ToString;
import ru.practicum.model.status.StateEvent;

import java.time.LocalDateTime;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;
    private String annotation;
    private String description;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private LocalDateTime time;
    @Enumerated(EnumType.STRING)
    private StateEvent state;
}
