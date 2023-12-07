package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private Long id;
    @NotNull
    private String description;
    @NotNull
    private long requestId;
    private LocalDateTime created;
}
