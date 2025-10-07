package com.fintrack.service;

import com.fintrack.dto.request.CategoryRequest;
import com.fintrack.dto.response.CategoryResponse;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.model.Category;
import com.fintrack.model.User;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.model.enums.UserRole;
import com.fintrack.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .role(UserRole.USER)
                .build();

        category = Category.builder()
                .id(1L)
                .user(user)
                .name("Food")
                .type(TransactionType.EXPENSE)
                .color("#FF5733")
                .icon("utensils")
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Transport");
        categoryRequest.setType(TransactionType.EXPENSE);
        categoryRequest.setColor("#3498DB");
        categoryRequest.setIcon("car");
    }

    @Test
    @DisplayName("Should get all categories for user")
    void shouldGetAllCategoriesForUser() {
        // Given
        List<Category> categories = Arrays.asList(category);
        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(categoryRepository.findByUserId(anyLong())).thenReturn(categories);

        // When
        List<CategoryResponse> response = categoryService.getAllCategories(null);

        // Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Food");

        verify(userService).getCurrentUserEntity();
        verify(categoryRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() {
        // Given
        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(categoryRepository.existsByUserIdAndName(anyLong(), anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When
        CategoryResponse response = categoryService.createCategory(categoryRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Food");

        verify(categoryRepository).existsByUserIdAndName(1L, "Transport");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate category")
    void shouldThrowExceptionWhenCreatingDuplicateCategory() {
        // Given
        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(categoryRepository.existsByUserIdAndName(anyLong(), anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("A category with this name already exists");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should get category by id")
    void shouldGetCategoryById() {
        // Given
        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(category));

        // When
        CategoryResponse response = categoryService.getCategoryById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Food");

        verify(categoryRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found");
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategorySuccessfully() {
        // Given
        when(userService.getCurrentUserEntity()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(any(Category.class));

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository).findByIdAndUserId(1L, 1L);
        verify(categoryRepository).delete(category);
    }
}
