package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserCallJoin;
import com.jeniustech.funding_search_engine.enums.UserCallJoinTypeEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserCallJoinRepository extends JpaRepository<UserCallJoin, Long> {

    @Query("SELECT ucj FROM UserCallJoin ucj WHERE ucj.callData.id = :callId AND ucj.userData.id = :userId AND ucj.type = :type")
    Optional<UserCallJoin> findByReferenceIdAndUserIdAndType(Long callId, Long userId, UserCallJoinTypeEnum type);

    @Query("SELECT ucj.callData.id FROM UserCallJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = :type")
    List<Long> findByUserIdAndType(Long userId, UserCallJoinTypeEnum type, Pageable pageable);

    @Query("SELECT ucj.callData.id FROM UserCallJoin ucj WHERE ucj.userData.id = :userId AND ucj.callData.id IN :ids AND ucj.type = :type")
    List<Long> findByReferenceIdsAndType(Long userId, List<Long> ids, UserCallJoinTypeEnum type);

    @Query("SELECT COUNT(ucj) FROM UserCallJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = :type")
    Long countByUserIdAndType(Long userId, UserCallJoinTypeEnum type);

}
