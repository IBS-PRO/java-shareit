package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestDtoTest {

    @Test
    void testSerialize() {

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("testText")
                .created(LocalDateTime.now())
                .requestId(1L)
                .build();

        ItemRequest itemRequest = ItemRequestMapper.requestDtoToRequest(itemRequestDto);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated());
        assertEquals(itemRequestDto.getRequestId(), itemRequest.getRequestId());

    }
}
