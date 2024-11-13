package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
