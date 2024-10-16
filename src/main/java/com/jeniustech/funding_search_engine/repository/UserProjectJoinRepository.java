package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserProjectJoin;
import com.jeniustech.funding_search_engine.enums.UserJoinTypeEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserProjectJoinRepository extends JpaRepository<UserProjectJoin, Long> {

    @Query("SELECT ucj FROM UserProjectJoin ucj WHERE ucj.projectData.id = :id AND ucj.userData.id = :userId AND ucj.type = :type")
    Optional<UserProjectJoin> findByReferenceIdAndUserIdAndType(Long id, Long userId, UserJoinTypeEnum type);

    @Query("SELECT ucj FROM UserProjectJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = :type")
    List<UserProjectJoin> findByUserIdAndType(Long userId, UserJoinTypeEnum type, Pageable pageable);

    @Query("SELECT ucj.projectData.id FROM UserProjectJoin ucj WHERE ucj.userData.id = :userId AND ucj.projectData.id IN :ids AND ucj.type = :type")
    List<Long> findByReferenceIdsAndType(Long userId, List<Long> ids, UserJoinTypeEnum type);

    @Query("SELECT COUNT(ucj) FROM UserProjectJoin ucj WHERE ucj.userData.id = :userId AND ucj.type = :type")
    Long countByUserIdAndType(Long userId, UserJoinTypeEnum type);

}
