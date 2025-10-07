package com.fintrack.repository;

import com.fintrack.model.Transaction;
import com.fintrack.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Transaction> findByFilters(@Param("userId") Long userId,
                                     @Param("type") TransactionType type,
                                     @Param("categoryId") Long categoryId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("search") String search,
                                     Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate " +
           "AND t.date <= :endDate")
    BigDecimal sumByUserAndTypeAndDateBetween(@Param("userId") Long userId,
                                               @Param("type") TransactionType type,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.category.id = :categoryId " +
           "AND t.type = :type " +
           "AND t.date >= :startDate " +
           "AND t.date <= :endDate")
    BigDecimal sumByUserAndCategoryAndTypeAndDateBetween(@Param("userId") Long userId,
                                                          @Param("categoryId") Long categoryId,
                                                          @Param("type") TransactionType type,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(t) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.date >= :startDate " +
           "AND t.date <= :endDate")
    long countByUserAndDateBetween(@Param("userId") Long userId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}