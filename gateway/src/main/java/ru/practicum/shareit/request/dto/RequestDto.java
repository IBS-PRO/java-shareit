package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class RequestDto {
    private Long id;
    @NotNull(message = "Поле \"name\" не может быть пустым")
    private String description;
    @NotNull(message = "Поле \"name\" обязательное")
    private long requestorId;
    private LocalDateTime created;
}
