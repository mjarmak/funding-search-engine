package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.AdminLogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_log_book")
public class AdminLogBook {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private AdminLogType type;

    private String logText;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
