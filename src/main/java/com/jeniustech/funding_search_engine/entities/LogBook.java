package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Builder
@Entity
@RequiredArgsConstructor
public class LogBook {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @Enumerated(EnumType.ORDINAL)
    private LogTypeEnum type;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
