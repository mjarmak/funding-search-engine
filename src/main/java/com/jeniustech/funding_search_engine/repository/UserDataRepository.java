package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findBySubjectId(String subjectId);

    Optional<UserData> findByUserName(String userName);
}
