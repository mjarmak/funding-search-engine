package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserCallJoin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserCallJoinRepository extends JpaRepository<UserCallJoin, Long> {

    @Query("SELECT ucj FROM UserCallJoin ucj WHERE ucj.callData.id = ?1 AND ucj.userData.id = ?2 AND ucj.type = 1")
    Optional<UserCallJoin> findFavoriteByCallAndUserId(Long callId, Long userId);

    @Query("SELECT ucj FROM UserCallJoin ucj WHERE ucj.userData.id = ?1 AND ucj.type = 1")
    List<UserCallJoin> findFavoritesByUserId(Long userId, Pageable pageable);

}
