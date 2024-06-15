package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.LogBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogBookRepository extends JpaRepository<LogBook, Long> {
}
