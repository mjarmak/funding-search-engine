package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.LogBook;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogBookRepository extends JpaRepository<LogBook, Long> {

    @Query("SELECT COUNT(l) FROM LogBook l WHERE l.userData.id = :id AND l.type = :logTypeEnum")
    Long countByUserDataIdAndType(Long id, LogTypeEnum logTypeEnum);

    @Query("SELECT l FROM LogBook l WHERE l.userData.id = :id AND l.type = :logTypeEnum ORDER BY l.createdAt DESC LIMIT :limit")
    List<LogBook> findByUserDataIdAndType(Long id, LogTypeEnum logTypeEnum, int limit);

    @Query("SELECT l FROM LogBook l WHERE l.userData.id = :userId AND l.type = :logTypeEnum ORDER BY l.createdAt DESC LIMIT 1")
    LogBook findLastByUserDataIdAndType(Long userId, LogTypeEnum logTypeEnum);
}
