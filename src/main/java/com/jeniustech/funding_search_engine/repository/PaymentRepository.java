package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
