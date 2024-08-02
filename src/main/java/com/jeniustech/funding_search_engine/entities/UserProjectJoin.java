package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.UserJoinTypeEnum;
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
@Table(name = "user_project_join")
public class UserProjectJoin {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project projectData;

    @Enumerated(EnumType.ORDINAL)
    private UserJoinTypeEnum type;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;


}
