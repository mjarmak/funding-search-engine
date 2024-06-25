package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
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

    @Column(length = 255)
    private String logText;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
