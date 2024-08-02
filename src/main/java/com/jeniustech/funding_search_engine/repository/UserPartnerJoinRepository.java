package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserPartnerJoin;
import com.jeniustech.funding_search_engine.enums.UserJoinTypeEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserPartnerJoinRepository extends JpaRepository<UserPartnerJoin, Long> {

    @Query("SELECT ucj FROM UserPartnerJoin ucj WHERE ucj.partnerData.id = :callId AND ucj.userData.id = :userId AND ucj.type = :type")
    Optional<UserPartnerJoin> findByReferenceIdAndUserIdAndType(Long callId, Long userId, UserJoinTypeEnum type);

    @Query("SELECT ucj FROM UserPartnerJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = :type")
    List<UserPartnerJoin> findByUserIdAndType(Long userId, UserJoinTypeEnum type, Pageable pageable);

    @Query("SELECT ucj.partnerData.id FROM UserPartnerJoin ucj WHERE ucj.userData.id = :userId AND ucj.partnerData.id IN :ids AND ucj.type = :type")
    List<Long> findByReferenceIdsAndType(Long userId, List<Long> ids, UserJoinTypeEnum type);

    @Query("SELECT COUNT(ucj) FROM UserPartnerJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = :type")
    Long countByUserIdAndType(Long userId, UserJoinTypeEnum type);

}
