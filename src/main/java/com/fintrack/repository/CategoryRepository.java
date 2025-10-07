package com.fintrack.repository;

import com.fintrack.model.Category;
import com.fintrack.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByUserId(Long userId);
    
    List<Category> findByUserIdAndType(Long userId, TransactionType type);
    
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    
    boolean existsByUserIdAndName(Long userId, String name);
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.user.id = :userId AND c.name = :name AND c.id != :categoryId")
    boolean existsByUserIdAndNameAndIdNot(@Param("userId") Long userId, 
                                          @Param("name") String name, 
                                          @Param("categoryId") Long categoryId);
}
