package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.UserCallJoinTypeEnum;
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
@Table(name = "user_organisation_join")
public class UserPartnerJoin {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation partnerData;

    @Enumerated(EnumType.ORDINAL)
    private UserCallJoinTypeEnum type;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;


}
