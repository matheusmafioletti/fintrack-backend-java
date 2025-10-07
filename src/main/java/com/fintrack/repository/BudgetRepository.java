package com.fintrack.repository;

import com.fintrack.model.Budget;
import com.fintrack.model.enums.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByUserId(Long userId);
    
    List<Budget> findByUserIdAndPeriod(Long userId, BudgetPeriod period);
    
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT COUNT(b) > 0 FROM Budget b WHERE b.user.id = :userId " +
           "AND b.category.id = :categoryId AND b.period = :period")
    boolean existsByUserIdAndCategoryIdAndPeriod(@Param("userId") Long userId,
                                                  @Param("categoryId") Long categoryId,
                                                  @Param("period") BudgetPeriod period);
    
    @Query("SELECT COUNT(b) > 0 FROM Budget b WHERE b.user.id = :userId " +
           "AND b.category.id = :categoryId AND b.period = :period AND b.id != :budgetId")
    boolean existsByUserIdAndCategoryIdAndPeriodAndIdNot(@Param("userId") Long userId,
                                                          @Param("categoryId") Long categoryId,
                                                          @Param("period") BudgetPeriod period,
                                                          @Param("budgetId") Long budgetId);
}
