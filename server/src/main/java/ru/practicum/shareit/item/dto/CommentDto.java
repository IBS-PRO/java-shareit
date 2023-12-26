package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String authorName;
    private LocalDateTime created;
    @NotBlank
    private String text;
    private Long itemId;
    private Long userId;
}
