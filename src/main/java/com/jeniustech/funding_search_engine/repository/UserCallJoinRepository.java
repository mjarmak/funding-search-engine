package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserCallJoin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserCallJoinRepository extends JpaRepository<UserCallJoin, Long> {

    @Query("SELECT ucj FROM UserCallJoin ucj WHERE ucj.callData.id = :callId AND ucj.userData.id = :userId AND ucj.type = 0")
    Optional<UserCallJoin> findFavoriteByCallAndUserId(Long callId, Long userId);

    @Query("SELECT ucj FROM UserCallJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = 0")
    List<UserCallJoin> findFavoritesByUserId(Long userId, Pageable pageable);

    @Query("SELECT ucj.callData.id FROM UserCallJoin ucj WHERE ucj.userData.id = :userId AND ucj.id IN :ids AND ucj.type = 0")
    List<Long> findFavoriteByCallIds(Long userId, List<Long> ids);

    @Query("SELECT COUNT(ucj) FROM UserCallJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = 0")
    Long countFavoritesByUserId(Long userId);
}
