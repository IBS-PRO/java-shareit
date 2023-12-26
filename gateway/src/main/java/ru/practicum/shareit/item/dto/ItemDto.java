package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    @NotNull(groups = Update.class)
    private Long id;
    @NotBlank(message = "Поле \"name\" не может быть пустым", groups = Create.class)
    private String name;
    @NotBlank(message = "Поле \"description\" не может быть пустым", groups = Create.class)
    private String description;
    @NotNull(message = "Поле \"available\" обязательное", groups = Create.class)
    private Boolean available;
    private Long requestId;

}
