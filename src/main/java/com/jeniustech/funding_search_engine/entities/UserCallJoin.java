package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.UserCallJoinTypeEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@Entity
@RequiredArgsConstructor
@Table(name = "user_call_join")
public class UserCallJoin {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "call_id")
    private Call callData;

    @Enumerated(EnumType.ORDINAL)
    private UserCallJoinTypeEnum type;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;


}
