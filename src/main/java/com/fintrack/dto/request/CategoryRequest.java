package com.fintrack.dto.request;

import com.fintrack.model.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be in hexadecimal format (#RRGGBB)")
    private String color;

    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;
}
