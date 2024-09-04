package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.model.status.StateEvent;

import java.time.LocalDateTime;

@Entity
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "event_sequence", initialValue = 0, allocationSize = 1)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 120)
    private String title;
    @Column(length = 2000)
    private String annotation;
    @Column(length = 7000)
    private String description;
    private boolean paid;
    private Boolean requestModeration;
    private Long participantLimit;
    private LocalDateTime time;
    @Enumerated(EnumType.STRING)
    private StateEvent state;

    @ToString.Exclude
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "location_lat", referencedColumnName = "lat"),
            @JoinColumn(name = "location_lon", referencedColumnName = "lon")
    })
    private Location location;
    private LocalDateTime createdOn;
    @Column(nullable = true)
    private LocalDateTime publishedOn;
}