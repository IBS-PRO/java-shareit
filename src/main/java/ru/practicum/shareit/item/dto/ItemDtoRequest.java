package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemDtoRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private ItemRequest request;
    private BookingDtoResponseShort lastBooking;
    private BookingDtoResponseShort nextBooking;
    private List<CommentDto> comments;
}
