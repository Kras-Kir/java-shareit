package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestInputDto {
    @NotBlank(message = "Description cannot be blank")
    private String description;
}