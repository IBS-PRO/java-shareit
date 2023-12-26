package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;
    private String authorName;
    private LocalDateTime created;
    @NotBlank(message = "Поле \"text\" не может быть пустым")
    private String text;
    private Long itemId;
    private Long userId;

}
