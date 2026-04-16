package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
}