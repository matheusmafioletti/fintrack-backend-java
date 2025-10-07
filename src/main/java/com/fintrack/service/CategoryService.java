package com.fintrack.service;

import com.fintrack.dto.request.CategoryRequest;
import com.fintrack.dto.response.CategoryResponse;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.exception.UnauthorizedException;
import com.fintrack.model.Category;
import com.fintrack.model.User;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(TransactionType type) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting all categories for user: {}", currentUser.getEmail());

        List<Category> categories;
        if (type != null) {
            categories = categoryRepository.findByUserIdAndType(currentUser.getId(), type);
        } else {
            categories = categoryRepository.findByUserId(currentUser.getId());
        }

        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting category {} for user: {}", id, currentUser.getEmail());

        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return mapToCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Creating category for user: {}", currentUser.getEmail());

        if (categoryRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
            throw new BadRequestException("A category with this name already exists");
        }

        Category category = Category.builder()
                .user(currentUser)
                .name(request.getName())
                .type(request.getType())
                .color(request.getColor() != null ? request.getColor() : "#808080")
                .icon(request.getIcon() != null ? request.getIcon() : "default")
                .build();

        category = categoryRepository.save(category);
        log.info("Category created with id: {}", category.getId());

        return mapToCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Updating category {} for user: {}", id, currentUser.getEmail());

        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Check if name is being changed and if it already exists
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByUserIdAndNameAndIdNot(currentUser.getId(), request.getName(), id)) {
            throw new BadRequestException("A category with this name already exists");
        }

        category.setName(request.getName());
        category.setType(request.getType());
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }

        category = categoryRepository.save(category);
        log.info("Category updated: {}", category.getId());

        return mapToCategoryResponse(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Deleting category {} for user: {}", id, currentUser.getEmail());

        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        categoryRepository.delete(category);
        log.info("Category deleted: {}", id);
    }

    @Transactional
    public void createDefaultCategories(User user) {
        log.info("Creating default categories for user: {}", user.getEmail());

        // Default expense categories
        createDefaultCategory(user, "Alimentação", TransactionType.EXPENSE, "#FF5733", "utensils");
        createDefaultCategory(user, "Transporte", TransactionType.EXPENSE, "#3498DB", "car");
        createDefaultCategory(user, "Moradia", TransactionType.EXPENSE, "#2ECC71", "home");
        createDefaultCategory(user, "Saúde", TransactionType.EXPENSE, "#E74C3C", "heartbeat");
        createDefaultCategory(user, "Educação", TransactionType.EXPENSE, "#9B59B6", "graduation-cap");
        createDefaultCategory(user, "Lazer", TransactionType.EXPENSE, "#F39C12", "gamepad");
        createDefaultCategory(user, "Outros", TransactionType.EXPENSE, "#95A5A6", "ellipsis");

        // Default income categories
        createDefaultCategory(user, "Salário", TransactionType.INCOME, "#27AE60", "dollar-sign");
        createDefaultCategory(user, "Freelance", TransactionType.INCOME, "#16A085", "briefcase");
        createDefaultCategory(user, "Investimentos", TransactionType.INCOME, "#2980B9", "chart-line");
        createDefaultCategory(user, "Outros", TransactionType.INCOME, "#7F8C8D", "plus-circle");

        log.info("Default categories created for user: {}", user.getEmail());
    }

    private void createDefaultCategory(User user, String name, TransactionType type, String color, String icon) {
        Category category = Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .color(color)
                .icon(icon)
                .build();
        categoryRepository.save(category);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .color(category.getColor())
                .icon(category.getIcon())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
