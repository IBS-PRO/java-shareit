package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest requestDtoToRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("Аргументы itemRequestDto не могут быть null");
        }
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestId(itemRequestDto.getRequestId())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemResponseDto requestToRequestReturnDto(ItemRequest itemRequest) {
        return ItemResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestId(itemRequest.getRequestId())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }

    public static List<ItemResponseDto> requestListToRequestReturnDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::requestToRequestReturnDto)
                .collect(Collectors.toList());
    }

}
