package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private long id;
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

    public void setLastBooking(Booking booking) {
        this.lastBooking = BookingMapper.toBookingShortDto(booking);
    }

    public void setNextBooking(Booking booking) {
        this.nextBooking = BookingMapper.toBookingShortDto(booking);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
