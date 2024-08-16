package ru.practicum.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @OneToOne
    @JoinColumn(name = "category_id")
    private EventCategory category;

    private String title;
    private String annotation;
    private String description;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private LocalDateTime time;
    @Enumerated(EnumType.STRING)
    private State state;

    public enum State {
        PUBLISHED,
        CANCEL_REVIEW
    }
}
