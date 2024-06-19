package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "log_book")
public class LogBook {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @Enumerated(EnumType.ORDINAL)
    private LogTypeEnum type;

    private String logText;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
