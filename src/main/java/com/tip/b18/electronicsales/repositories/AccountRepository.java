package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>{
    Optional<Account> findByUserName(String userName);
    @Query("SELECT a FROM Account a WHERE a.userName LIKE CONCAT('%', :search, '%') OR a.phoneNumber LIKE CONCAT('%', :search, '%') ORDER BY a.createdAt DESC")
    Page<Account> findByUserNameOrPhoneNumber(@Param("search") String search, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    @Query("SELECT COUNT(a) FROM Account a WHERE a.createdAt > :startDay AND a.createdAt < :endDay")
    int countQuantityNewCustomers(LocalDateTime startDay, LocalDateTime endDay);
    Account findByEmail(String email);
}
