package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
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
@Table(name = "long_text")
public class LongText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "call_id")
    private Call call;


    @Enumerated(EnumType.ORDINAL)
    private LongTextTypeEnum type;

    private String text;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    public String toString() {
        return "LongText(id=" + this.getId() + ", type=" + this.getType() + ")";
    }

}
